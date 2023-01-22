package com.dongchyeon.exerciseintervaltimer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var job: Job? = null

    var minute: Int = 0
    var second = 10

    private var totalTime: Int = minute * 60 + second
    private val _remainTime = totalTime
    var remainTime: Int = _remainTime

    init {
        viewModelScope.launch {
            _timerState.update {
                it.copy(
                    remainTime = remainTime,
                    totalTime = totalTime
                )
            }
        }
    }

    fun startTimer() {
        if (remainTime <= 0) remainTime = totalTime
        job = viewModelScope.launch {
            countDown()
                .onCompletion { _timerState.update { it.copy(isRunning = false) } }
                .collect { _timerState.emit(it) }
        }
    }

    fun stopTimer() {
        viewModelScope.launch { _timerState.update { it.copy(isRunning = false) } }
        job?.cancel()
    }

    fun setTimer(minute: Int, second: Int) {
        job?.cancel()

        this.minute = minute
        this.second = second
        totalTime = minute * 60 + second
        remainTime = totalTime

        viewModelScope.launch {
            _timerState.update {
                it.copy(
                    remainTime = remainTime,
                    totalTime = totalTime,
                    isRunning = false
                )
            }
        }
    }

    private fun countDown(): Flow<TimerState> =
        (remainTime - 1 downTo 0).asFlow()
            .onEach {
                delay(1000)
                remainTime--
            }
            .onStart { emit(remainTime) }
            .conflate()
            .transform { remainTime: Int ->
                emit(TimerState(remainTime, totalTime, true))
            }
}