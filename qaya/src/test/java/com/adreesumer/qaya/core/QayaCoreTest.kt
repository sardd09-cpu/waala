package com.adreesumer.qaya.core

import com.adreesumer.qaya.events.EventBus
import com.adreesumer.qaya.system.SystemManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class QayaCoreTest {

    @Before
    fun setup() {
        EventBus.clear()
    }

    @Test
    fun testCoreInitialization() {
        val core = QayaCore()
        assertEquals(CoreState.IDLE, core.state)

        var eventReceived = false
        EventBus.subscribe { event ->
            if (event is com.adreesumer.qaya.events.SystemInitializedEvent) {
                eventReceived = true
            }
        }

        core.initialize()

        assertEquals(CoreState.READY, core.state)
        assertTrue("SystemInitializedEvent should have been published", eventReceived)
    }

    @Test
    fun testSystemManagerBoot() {
        val core = QayaCore()
        val manager = SystemManager(core)

        assertEquals("System is Idle.", manager.getSystemStatus())

        manager.bootSystem()

        assertEquals("System is Ready.", manager.getSystemStatus())
        assertEquals(CoreState.READY, core.state)
    }
}
