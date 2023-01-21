package com.dongchyeon.exerciseintervaltimer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private var job: Job? = null

    private val _minute = 0
    var minute: Int = _minute

    private val _second = 10
    var second: Int = _second

    private val _remainSeconds = 10
    var remainSeconds: Int = _remainSeconds

    var totalSeconds: Int = minute * 60 + second

    private val _timerState = MutableStateFlow(TimerState(remainSeconds, totalSeconds))
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    fun startTimer() {
        if (remainSeconds <= 0) remainSeconds = totalSeconds
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

    private fun countDown(): Flow<TimerState> =
        (remainSeconds - 1 downTo 0).asFlow()
            .onEach {
                delay(1000)
                remainSeconds--
            }
            .onStart { emit(remainSeconds) }
            .conflate()
            .transform { remainSeconds: Int ->
                emit(TimerState(remainSeconds, totalSeconds, true))
            }
}