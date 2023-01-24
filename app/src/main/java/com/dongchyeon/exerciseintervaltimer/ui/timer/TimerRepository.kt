package com.dongchyeon.exerciseintervaltimer.ui.timer

import android.os.SystemClock
import com.dongchyeon.exerciseintervaltimer.util.toMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlin.properties.Delegates

class TimerRepository {
    companion object {
        val DEFAULT_STATE = TimerDataState(
            allSets = 3,
            prepareSec = 30,
            exerciseSec = 30,
            restSec = 30,
        )
    }

    private val _dataState = MutableStateFlow(DEFAULT_STATE)
    val dataState = _dataState.asSharedFlow()

    private var isRunning = false
    private var isFinished = false

    fun setTimer(
        allSets: Int,
        prepareSec: Int,
        exerciseSec: Int,
        restSec: Int
    ) {
        _dataState.value = TimerDataState(
            allSets = allSets,
            prepareSec = prepareSec,
            exerciseSec = exerciseSec,
            restSec = restSec
        )
    }

    suspend fun restart() {
        if (isFinished) {
            isFinished = false
            _dataState.update {
                it.copy(
                    currentSet = 1,
                    currentRoundRemainMillis = it.prepareSec.toMillis(),
                    totalRemainMillis = (it.prepareSec + (it.exerciseSec + it.restSec) * it.allSets - it.restSec).toMillis(),
                    roundState = TimerRoundState.PREPARE,
                    isFinished = false
                )
            }
        }

        isRunning = true
        _dataState.update { it.copy(isRunning = true) }

        flow {
            while (isRunning) {
                val startTime = SystemClock.elapsedRealtime()

                delay(25)

                val elapsedTime = SystemClock.elapsedRealtime() - startTime
                val currentLeftTime = _dataState.value.currentRoundRemainMillis - elapsedTime
                val totalLeftTime = _dataState.value.totalRemainMillis - elapsedTime

                _dataState.value = _dataState.value.copy(
                    currentRoundRemainMillis = currentLeftTime,
                    totalRemainMillis = totalLeftTime
                )

                if (totalLeftTime <= 0) {   // 모든 세트 종료
                    pause()
                    isFinished = true
                    _dataState.value = _dataState.value.copy(
                        isFinished = true
                    )
                }

                if (currentLeftTime <= 0) {  // 현재 라운드 종료
                    var roundTotalTime by Delegates.notNull<Long>()
                    var roundRemainTime by Delegates.notNull<Long>()

                    val nextRound = when (_dataState.value.roundState) {
                        TimerRoundState.PREPARE -> TimerRoundState.EXERCISE
                        TimerRoundState.EXERCISE -> TimerRoundState.REST
                        TimerRoundState.REST -> TimerRoundState.EXERCISE
                    }

                    val set =
                        if (nextRound == TimerRoundState.REST) _dataState.value.currentSet - 1 else _dataState.value.currentSet
                    when (nextRound) {
                        TimerRoundState.PREPARE -> {
                            roundTotalTime = _dataState.value.prepareSec.toMillis()
                            roundRemainTime = _dataState.value.prepareSec.toMillis()
                        }
                        TimerRoundState.EXERCISE -> {
                            roundTotalTime = _dataState.value.exerciseSec.toMillis()
                            roundRemainTime = _dataState.value.exerciseSec.toMillis()
                        }
                        TimerRoundState.REST -> {
                            roundTotalTime = _dataState.value.restSec.toMillis()
                            roundRemainTime = _dataState.value.restSec.toMillis()
                        }
                    }

                    _dataState.value = _dataState.value.copy(
                        currentSet = set,
                        currentRoundTotalMillis = roundTotalTime,
                        currentRoundRemainMillis = roundRemainTime,
                        roundState = nextRound
                    )
                }

                emit(_dataState.value)
            }
        }.collect { state ->
            _dataState.value = state
        }
    }

    fun pause() {
        isRunning = false
        _dataState.update { it.copy(isRunning = false) }
    }
}