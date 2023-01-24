package com.dongchyeon.exerciseintervaltimer.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dongchyeon.exerciseintervaltimer.ui.timer.TimerRepository.Companion.DEFAULT_STATE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val repository: TimerRepository
) : ViewModel() {
    private val _timerDataState =
        MutableStateFlow(DEFAULT_STATE)
    val timerDataState: StateFlow<TimerDataState> = _timerDataState

    private var job: Job? = null

    init {
        viewModelScope.launch {
            repository.dataState
                .collect { state ->
                    _timerDataState.value = state
                }
        }
    }

    fun setTimer(
        allSets: Int,
        prepareTime: Int,
        exerciseTime: Int,
        restTime: Int
    ) {
        job?.cancel()
        repository.setTimer(allSets, prepareTime, exerciseTime, restTime)
    }

    fun startTimer() {
        job = viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.restart()
            }
        }
    }

    fun pauseTimer() {
        job?.cancel()
        repository.pause()
    }

}