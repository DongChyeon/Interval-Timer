package com.dongchyeon.exerciseintervaltimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.dongchyeon.exerciseintervaltimer.ui.theme.ExerciseIntervalTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExerciseIntervalTimerTheme {
                Surface(Modifier
                    .fillMaxSize()
                ) {
                    TimerScreen()
                }
            }
        }
    }
}