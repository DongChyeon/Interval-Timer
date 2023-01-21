package com.dongchyeon.exerciseintervaltimer

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    val remainTime: Int = viewModel.remainTime
    val totalTime: Int = viewModel.totalTime
    val isRunning: Boolean = viewModel.isRunning

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CircularTimer(
                    remainTime = remainTime,
                    totalTime = totalTime
                )
                Button(
                    onClick = {
                        if (!isRunning) viewModel.startTimer() else viewModel.stopTimer()
                    }
                ) {
                    Text(
                        text = if (!isRunning) "시작" else "정지",
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
    activeBarColor: Color = Color.Blue,
    inactiveBarColor: Color = Color.LightGray,
) {
    /*
    val progress = remember { Animatable(remainTime / totalTime.toFloat()) }

    LaunchedEffect(isRunning) {
        progress.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = remainTime * 1000,
                easing = LinearEasing
            )
        )
    }
    */

    val progress = remainTime / totalTime.toFloat()

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