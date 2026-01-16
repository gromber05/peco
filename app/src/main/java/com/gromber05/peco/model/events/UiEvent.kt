package com.gromber05.peco.model.events

sealed interface UiEvent {
    data object LoggedOut : UiEvent
    data class Error(val message: String) : UiEvent
    data class Success(val message: String) : UiEvent
}
