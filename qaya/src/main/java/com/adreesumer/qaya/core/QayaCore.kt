package com.adreesumer.qaya.core

import com.adreesumer.qaya.events.EventBus
import com.adreesumer.qaya.events.SystemInitializedEvent

enum class CoreState {
    IDLE,
    INITIALIZING,
    READY,
    ERROR
}

class QayaCore {
    var state: CoreState = CoreState.IDLE
        private set

    fun initialize() {
        if (state == CoreState.INITIALIZING || state == CoreState.READY) {
            return
        }

        state = CoreState.INITIALIZING

        try {
            // Initialize basic core components here

            state = CoreState.READY
            EventBus.publish(SystemInitializedEvent())
        } catch (e: Exception) {
            state = CoreState.ERROR
            // TODO: Log error or publish ErrorEvent
        }
    }
}
