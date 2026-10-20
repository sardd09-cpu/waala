package com.adreesumer.qaya.system

import com.adreesumer.qaya.core.QayaCore
import com.adreesumer.qaya.core.CoreState

class SystemManager(private val core: QayaCore) {

    fun bootSystem() {
        core.initialize()
    }

    fun getSystemStatus(): String {
        return when (core.state) {
            CoreState.READY -> "System is Ready."
            CoreState.ERROR -> "System Initialization Failed."
            CoreState.INITIALIZING -> "System is Booting..."
            CoreState.IDLE -> "System is Idle."
        }
    }
}
