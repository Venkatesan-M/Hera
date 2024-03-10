package com.example.hera.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class StopWatchViewModel: ViewModel() {
    private val _elapsedTime = MutableStateFlow(0)
    private val _timeState = MutableStateFlow(TimerState.RESET)
    val timerState = _timeState.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS")
    val stopWatchText = _elapsedTime
        .map { millis-> LocalTime.ofNanoOfDay((millis * 1_000_000).toLong()).format(formatter) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = "00:00:00:000"
        )

    init {
        _timeState
            .flatMapLatest { timerState ->
                getTimerFlow(
                    isRunning = timerState == TimerState.RUNNING
                )
            }
            .onEach { timeDiff ->
                _elapsedTime.update { (it + timeDiff).toInt() }
            }
            .launchIn(viewModelScope)
    }

    fun toggleIsRunning(){
        when(timerState.value){
            TimerState.RUNNING -> _timeState.update { TimerState.PAUSED }
            TimerState.PAUSED ,
            TimerState.RESET -> _timeState.update { TimerState.RUNNING }
        }
    }

    fun resetTimer(){
        _timeState.update { TimerState.RESET }
        _elapsedTime.update { 0 }
    }

    private  fun getTimerFlow(isRunning: Boolean): Flow<Long> {
        return flow {
            var startMillis = System.currentTimeMillis()
            while (isRunning){
                val currentMillis = System.currentTimeMillis()
                val timeDiff = if(currentMillis > startMillis){
                    currentMillis - startMillis
                } else 0L
                emit(timeDiff)
                startMillis = System.currentTimeMillis()
                delay(10L)
            }
        }
    }
}