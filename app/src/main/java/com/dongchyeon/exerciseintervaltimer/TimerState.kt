package com.dongchyeon.exerciseintervaltimer

data class TimerState(
    val remainSeconds: Int,
    val totalSeconds: Int,
    val isRunning: Boolean = false,
    val progress: Float = remainSeconds / totalSeconds.toFloat()
)