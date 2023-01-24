package com.dongchyeon.exerciseintervaltimer.timer

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TimerViewModel : ViewModel() {
    private val _timerUiState =
        MutableStateFlow(TimerUiState(1, 3, 30, 30, 90, TimerSetState.PREPARE, false))
    val timerUiState: StateFlow<TimerUiState> = _timerUiState.asStateFlow()

    private var job: Job? = null

    var minute: Int = 0
    var second = 10

    private var totalTime: Int = minute * 60 + second
    private var remainTime = totalTime

    init {
        viewModelScope.launch {
            _timerUiState.update {
                it.copy(
                    currentSetRemainTime = remainTime,
                    currentSetTotalTime = totalTime
                )
            }
        }
    }

    fun startTimer() {
        if (remainTime <= 0) remainTime = totalTime
        job = viewModelScope.launch {
            countDown()
                .onCompletion { _timerUiState.update { it.copy(isRunning = false) } }
                .collect { remainTime -> _timerUiState.update { it.copy(currentSetRemainTime = remainTime) } }
        }
    }

    fun stopTimer() {
        viewModelScope.launch { _timerUiState.update { it.copy(isRunning = false) } }
        job?.cancel()
    }

    fun setTimer(minute: Int, second: Int) {
        job?.cancel()

        this.minute = minute
        this.second = second
        totalTime = minute * 60 + second
        remainTime = totalTime

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _timerUiState.update {
                    it.copy(
                        currentSetRemainTime = remainTime,
                        currentSetTotalTime = totalTime,
                        runningState = TimerSetState.PREPARE
                    )
                }
            }
        }
    }

    private fun countDown(): Flow<Int> = flow {
        val startTime = SystemClock.elapsedRealtime()
        do {
            delay(100)
            val elapsedTime = ((SystemClock.elapsedRealtime() - startTime) / 1000).toInt()
            remainTime = totalTime - elapsedTime
            emit(remainTime)
        } while (remainTime > 0)
    }
}