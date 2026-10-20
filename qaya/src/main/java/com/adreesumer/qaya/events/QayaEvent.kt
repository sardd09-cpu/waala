package com.adreesumer.qaya.events

interface QayaEvent {
    val eventId: String
    val timestamp: Long
}

data class SystemInitializedEvent(
    override val eventId: String = java.util.UUID.randomUUID().toString(),
    override val timestamp: Long = System.currentTimeMillis()
) : QayaEvent
