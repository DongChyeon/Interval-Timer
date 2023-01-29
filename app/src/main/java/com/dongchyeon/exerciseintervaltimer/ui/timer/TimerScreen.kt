package com.dongchyeon.exerciseintervaltimer.ui.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dongchyeon.exerciseintervaltimer.ui.BottomSheet
import com.dongchyeon.exerciseintervaltimer.ui.theme.ExerciseIntervalTimerTheme
import com.dongchyeon.exerciseintervaltimer.ui.theme.Purple700
import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerRepository.Companion.DEFAULT_STATE
import com.dongchyeon.exerciseintervaltimer.util.getFormattedTime
import com.dongchyeon.exerciseintervaltimer.util.toSec

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    timerDataState: TimerDataState,
    startTimer: () -> Unit,
    pauseTimer: () -> Unit,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    setTimer: (allSets: Int, prepareTime: Int, exerciseTime: Int, restTime: Int) -> Unit
) {
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetElevation = 8.dp,
        sheetShape = RoundedCornerShape(
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        sheetContent = {
            BottomSheet(modifier, bottomSheetScaffoldState, timerDataState, setTimer)
        },
        sheetPeekHeight = 50.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text("진행율")

                Spacer(modifier = modifier.height(4.dp))

                LinearProgressIndicator(
                    modifier = modifier
                        .width(320.dp)
                        .height(15.dp),
                    progress = 1 - (timerDataState.totalRemainMillis / timerDataState.totalMillis.toFloat()),
                    backgroundColor = Color.LightGray,
                    color = Purple700
                )

                Spacer(modifier = modifier.height(32.dp))

                CircularTimer(
                    remainMillis = timerDataState.currentRoundRemainMillis,
                    totalMillis = timerDataState.currentRoundTotalMillis,
                    roundState = timerDataState.roundState
                )

                Spacer(modifier = modifier.height(32.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {
                        if (!timerDataState.isRunning) startTimer() else pauseTimer()
                    }
                ) {
                    Text(
                        text = if (!timerDataState.isRunning) "시작" else "정지",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(300.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CircularTimer(
    modifier: Modifier = Modifier,
    remainMillis: Long,
    totalMillis: Long,
    roundState: TimerRoundState
) {
    val progress = remainMillis / totalMillis.toFloat()
    val activeBarColor = when (roundState) {
        TimerRoundState.PREPARE -> Color.DarkGray
        TimerRoundState.EXERCISE -> Color.Blue
        TimerRoundState.REST -> Color.Green
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = modifier
                .size(300.dp)
        ) {
            drawArc(
                color = Color.LightGray,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                size = Size(width = size.width, height = size.height),
                topLeft = Offset(0F, 0F),
                style = Stroke(30.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = activeBarColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                size = Size(width = size.width, height = size.height),
                topLeft = Offset(0F, 0F),
                style = Stroke(30.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (roundState) {
                    TimerRoundState.PREPARE -> "준비"
                    TimerRoundState.EXERCISE -> "운동"
                    TimerRoundState.REST -> "휴식"
                },
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
            )

            Text(
                text = "${getFormattedTime(value = (remainMillis.toSec() / 60) % 60)}:" +
                        getFormattedTime(value = remainMillis.toSec() % 60),
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun PreviewTimer() {
    ExerciseIntervalTimerTheme {
        TimerScreen(
            timerDataState = DEFAULT_STATE,
            startTimer = { },
            pauseTimer = { },
            bottomSheetScaffoldState = BottomSheetScaffoldState(
                drawerState = DrawerState(DrawerValue.Closed),
                bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed),
                snackbarHostState = SnackbarHostState()
            ),
            setTimer = { _, _, _, _ -> }
        )
    }
}