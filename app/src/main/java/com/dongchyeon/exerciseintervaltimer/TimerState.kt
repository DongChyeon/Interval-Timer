package com.dongchyeon.exerciseintervaltimer

data class TimerState(
    val remainTime: Int = 1,
    val totalTime: Int = 1,
    val isRunning: Boolean = false,
    val progress: Float = remainTime / totalTime.toFloat()
)