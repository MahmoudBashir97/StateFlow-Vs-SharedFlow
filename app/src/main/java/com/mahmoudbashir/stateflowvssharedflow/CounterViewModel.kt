package com.mahmoudbashir.stateflowvssharedflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class UiState(
    val isLoading: Boolean = false,
    val counter: Int = 0
)

sealed interface UiEvent {
    data class ShowToast(val message: String) : UiEvent
}

sealed interface Action {
    object IncrementCounter : Action
}


class CounterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    fun onAction(action: Action) = viewModelScope.launch {
        when (action) {
            Action.IncrementCounter -> {
                _uiState.update {
                    it.copy(isLoading = true)
                }
                delay(1000L)
                _uiState.update {
                    it.copy(isLoading = false, counter = it.counter.plus(1))
                }
                _uiEvent.emit(UiEvent.ShowToast("Counter incremented to ${_uiState.value.counter}"))
            }
        }
    }
}