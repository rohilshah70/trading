package com.example.trading.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * ViewModel with MVI architecture built-in.
 *
 * @param <State> State of the ViewModel.
 * @param <Event> Events associated with the ViewModel
 * @param <Effect> Effects associated with the ViewModel
 */
abstract class MviViewModel<State : MviState, Event : MviEvent, Effect : MviEffect>(
    initialState: State
) : ViewModel() {
    // State management.
    private val stateMutex = Mutex()
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    // Event management.
    private val _events: MutableSharedFlow<Event> = MutableSharedFlow()
    val events = _events.asSharedFlow()

    // Effect management.
    private val _effects: MutableSharedFlow<Effect> = MutableSharedFlow(replay = 0)
    val effects = _effects.asSharedFlow()

    fun currentState() = _state.value

    init {
        subscribeToEvents()
        subscribeToEffects()
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            events.collect {
                handleEvents(it)
            }
        }
    }

    private fun subscribeToEffects() {
        viewModelScope.launch {
            effects.collect {
                handleEffects(it)
            }
        }
    }

    protected suspend fun updateState(reducer: State.() -> State) {
        stateMutex.withLock {
            _state.value = reducer(_state.value)
        }
    }

    protected suspend fun updateState(state: State) {
        stateMutex.withLock {
            _state.value = state
        }
    }

    /**
     * Set new Event.
     *
     * @param event Event to be executed
     */
    fun setEvent(event: Event) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    /**
     * Set new Effect
     *
     * @param effect Effect to be executed
     */
    fun setEffect(effect: Effect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    /**
     * Override this to handle events submitted to the ViewModel.
     */
    protected abstract suspend fun handleEvents(event: Event)

    /**
     * Override this to handle effects submitted to the ViewModel.
     */
    protected abstract suspend fun handleEffects(effect: Effect)
}
