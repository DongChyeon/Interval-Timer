package com.dongchyeon.exerciseintervaltimer.ui.timer

import com.dongchyeon.exerciseintervaltimer.util.toMillis

data class TimerDataState(
    val currentSet: Int = 0,
    val allSets: Int,

    val prepareSec: Int,
    val exerciseSec: Int,
    val restSec: Int,

    val currentRoundTotalMillis: Long = prepareSec.toMillis(),
    val currentRoundRemainMillis: Long = prepareSec.toMillis(),

    val totalMillis: Long = (prepareSec + (exerciseSec + restSec) * allSets - restSec).toMillis(),
    val totalRemainMillis: Long = (prepareSec + (exerciseSec + restSec) * allSets - restSec).toMillis(),
    val roundState: TimerRoundState = TimerRoundState.PREPARE,

    val isRunning: Boolean = false,
    val isFinished: Boolean = false
)

enum class TimerRoundState {
    PREPARE, EXERCISE, REST
}
