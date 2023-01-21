package com.dongchyeon.exerciseintervaltimer

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TimerViewModel : ViewModel() {
    private var job: Job? = null

    private var _isRunning by mutableStateOf(false)
    var isRunning: Boolean
        get() = _isRunning
        set(value) { _isRunning = value }

    private var _minute by mutableStateOf(0)
    var minute: Int
        get() = _minute
        set(value) { _minute = value }

    private var _second by mutableStateOf(30)
    var second: Int
        get() = _second
        set(value) { _second = value }

    private var _remainTime by mutableStateOf(0)
    var remainTime: Int
        get() = _remainTime
        set(value) { _remainTime = value }

    var totalTime : Int = minute * 60 + second

    fun startTimer() {
        if (remainTime <= 0) remainTime = totalTime
        isRunning = true
        job = countDown().onEach {
            remainTime = it
        }.launchIn(viewModelScope)
    }

    fun stopTimer() {
        job?.cancel()
        isRunning = false
    }

    private fun countDown(): Flow<Int> = flow {
        var time = remainTime
        while (time > 0) {
            delay(1000)
            emit(--time)
        }
    }
}