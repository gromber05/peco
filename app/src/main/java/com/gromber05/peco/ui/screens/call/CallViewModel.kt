package com.gromber05.peco.ui.screens.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.CallRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CallUiState(
    val callId: String = "",
    val status: String = "idle", // idle | ringing | in_call | ended
    val connection: String = "",
    val muted: Boolean = false,
    val error: String? = null
)

class CallViewModel(
    private val repo: CallRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(CallUiState())
    val ui = _ui.asStateFlow()

    fun startCall(myUid: String, otherUid: String) {
        val callId = listOf(myUid, otherUid).sorted().joinToString("_")
        _ui.update { it.copy(callId = callId, status = "ringing") }

        viewModelScope.launch {
            try {
                repo.callerFlow(
                    callId = callId,
                    myUid = myUid,
                    otherUid = otherUid,
                    onState = { s -> _ui.update { it.copy(connection = s) } },
                    onError = { e -> _ui.update { it.copy(error = e) } }
                )
                _ui.update { it.copy(status = "in_call") }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message, status = "ended") }
            }
        }
    }

    fun acceptCall(callId: String) {
        _ui.update { it.copy(callId = callId, status = "in_call") }
        viewModelScope.launch {
            try {
                repo.calleeFlow(
                    callId = callId,
                    onState = { s -> _ui.update { it.copy(connection = s) } },
                    onError = { e -> _ui.update { it.copy(error = e) } }
                )
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message, status = "ended") }
            }
        }
    }

    fun toggleMute() {
        val newMuted = !_ui.value.muted
        repo.setMuted(newMuted)
        _ui.update { it.copy(muted = newMuted) }
    }

    fun hangUp() {
        val callId = _ui.value.callId
        viewModelScope.launch {
            runCatching { repo.endCall(callId) }
            _ui.update { it.copy(status = "ended") }
        }
    }
}
