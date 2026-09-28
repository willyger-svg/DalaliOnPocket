package com.example.core.sync

import com.example.data.model.DopNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RealtimeEvent(
    val channel: String, // e.g., "viewings", "agreements", "chat", "ledger"
    val entityId: String,
    val action: String,
    val payload: String,
    val timestamp: Long = System.currentTimeMillis()
)

object SyncManager {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _realtimeEvents = MutableSharedFlow<RealtimeEvent>(replay = 1)
    val realtimeEvents: SharedFlow<RealtimeEvent> = _realtimeEvents.asSharedFlow()

    private val _notifications = MutableStateFlow<List<DopNotification>>(
        listOf(
            DopNotification(
                title = "Karibu Dalalion Pocket",
                message = "Soko lako la kuaminika la nyumba na viwanja nchini Tanzania linafanya kazi kikamilifu."
            ),
            DopNotification(
                title = "DoP Express Ready",
                message = "Nyumba ya Masaki (DOP-TZA-DAR-000184) iko tayari kwa ukaguzi wa haraka na Guide."
            )
        )
    )
    val notifications: StateFlow<List<DopNotification>> = _notifications.asStateFlow()

    fun emitRealtimeEvent(channel: String, entityId: String, action: String, payload: String) {
        scope.launch {
            _realtimeEvents.emit(RealtimeEvent(channel, entityId, action, payload))
            addNotification(
                title = "Taarifa ya Moja kwa Moja ($action)",
                message = payload,
                relatedId = entityId
            )
        }
    }

    fun addNotification(title: String, message: String, relatedId: String? = null) {
        val newNotification = DopNotification(
            title = title,
            message = message,
            relatedEntityId = relatedId
        )
        _notifications.value = listOf(newNotification) + _notifications.value
    }

    fun toggleConnectionState() {
        _isConnected.value = !_isConnected.value
    }
}
