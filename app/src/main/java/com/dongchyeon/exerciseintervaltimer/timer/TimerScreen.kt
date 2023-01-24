package com.dongchyeon.exerciseintervaltimer.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dongchyeon.exerciseintervaltimer.BottomSheet
import com.dongchyeon.exerciseintervaltimer.ui.theme.ExerciseIntervalTimerTheme
import com.dongchyeon.exerciseintervaltimer.util.getFormattedTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel()
) {
    val timerUiState by viewModel.timerUiState.collectAsState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

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
            BottomSheet(modifier, bottomSheetScaffoldState, viewModel)
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
                CircularTimer(
                    remainTime = timerUiState.currentSetRemainTime,
                    totalTime = timerUiState.currentSetTotalTime,
                    setState = timerUiState.runningState
                )
                Button(
                    onClick = {
                        if (!timerUiState.isRunning) viewModel.startTimer() else viewModel.stopTimer()
                    }
                ) {
                    Text(
                        text = if (!timerUiState.isRunning) "시작" else "정지",
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
    remainTime: Int,
    totalTime: Int,
    setState: TimerSetState
) {
    val progress = remainTime / totalTime.toFloat()
    val activeBarColor = when (setState) {
        TimerSetState.PREPARE -> Color.DarkGray
        TimerSetState.EXERCISE -> Color.Blue
        TimerSetState.REST -> Color.Green
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
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                size = Size(width = size.width, height = size.height),
                topLeft = Offset(0F, 0F),
                style = Stroke(30.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = activeBarColor,
                startAngle = -215f,
                sweepAngle = 250f * progress,
                useCenter = false,
                size = Size(width = size.width, height = size.height),
                topLeft = Offset(0F, 0F),
                style = Stroke(30.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${getFormattedTime(value = (remainTime / 60) % 60)}:" +
                        getFormattedTime(value = remainTime % 60),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun PreviewTimer() {
    ExerciseIntervalTimerTheme {
        TimerScreen()
    }
}