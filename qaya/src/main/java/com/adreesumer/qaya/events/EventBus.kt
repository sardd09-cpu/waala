package com.adreesumer.qaya.events

object EventBus {
    private val listeners = mutableListOf<(QayaEvent) -> Unit>()

    fun publish(event: QayaEvent) {
        listeners.forEach { it.invoke(event) }
    }

    fun subscribe(listener: (QayaEvent) -> Unit) {
        listeners.add(listener)
    }

    fun clear() {
        listeners.clear()
    }
}
