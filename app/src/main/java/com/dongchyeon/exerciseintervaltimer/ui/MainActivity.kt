package com.dongchyeon.exerciseintervaltimer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dongchyeon.exerciseintervaltimer.ui.theme.ExerciseIntervalTimerTheme
import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerScreen
import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: TimerViewModel = viewModel()
            val timerDataState by viewModel.timerDataState.collectAsState()
            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
            )

            ExerciseIntervalTimerTheme {
                Surface(
                    Modifier
                        .fillMaxSize()
                ) {
                    TimerScreen(
                        timerDataState = timerDataState,
                        startTimer = { viewModel.startTimer() },
                        pauseTimer = { viewModel.pauseTimer() },
                        bottomSheetScaffoldState = bottomSheetScaffoldState,
                        setTimer = viewModel::setTimer
                    )
                }
            }
        }
    }
}