package com.dongchyeon.exerciseintervaltimer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dongchyeon.exerciseintervaltimer.ui.theme.ExerciseIntervalTimerTheme

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.timerState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                CircularTimer(
                    remainTime = timerState.remainSeconds,
                    progress = timerState.progress
                )
                Button(
                    onClick = {
                        if (!timerState.isRunning) viewModel.startTimer() else viewModel.stopTimer()
                    }
                ) {
                    Text(
                        text = if (!timerState.isRunning) "시작" else "정지",
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
    progress: Float,
    activeBarColor: Color = Color.Blue,
    inactiveBarColor: Color = Color.LightGray,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = modifier
                .size(300.dp)
        ) {
            drawArc(
                color = inactiveBarColor,
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                size = Size(width = size.width, height = size.height),
                topLeft = Offset(0F, 0F),
                style = Stroke(20.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = activeBarColor,
                startAngle = -215f,
                sweepAngle = 250f * progress,
                useCenter = false,
                size = Size(width = size.width, height = size.height),
                topLeft = Offset(0F, 0F),
                style = Stroke(20.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${getFormattedTime(value = (remainTime / 60) % 60)}:" +
                        getFormattedTime(value = remainTime % 60),
                fontSize = 48.sp
            )
        }
    }
}

private fun getFormattedTime(value: Int) : String {
    return String.format("%02d", value)
}

@Preview
@Composable
fun PreviewTimer() {
    ExerciseIntervalTimerTheme {
        Surface(Modifier
            .fillMaxSize()
        ) {
            TimerScreen()
        }
    }
}