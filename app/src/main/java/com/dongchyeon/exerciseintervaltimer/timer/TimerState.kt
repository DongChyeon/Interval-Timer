package com.dongchyeon.exerciseintervaltimer.timer

data class TimerUiState(
    val currentSet: Int,
    val allSets: Int,

    val currentSetTotalTime: Int,
    val currentSetRemainTime: Int,
    val totalRemainTime: Int,

    val runningState: TimerSetState,
    val isRunning: Boolean
)

data class TimerDataState(
    val currentSet: Int,
    val allSets: Int,

    val prepareTime: Int,
    val exerciseTime: Int,
    val restTime: Int,

    val currentSetTotalTime: Int = prepareTime,
    val currentSetRemainTime: Int = prepareTime,

    val totalRemainTime: Int = prepareTime + (exerciseTime + restTime) * allSets - restTime,
    val setState: TimerSetState
) {
    fun toTimerUiState() {

    }
}

enum class TimerSetState {
    PREPARE, EXERCISE, REST
}
