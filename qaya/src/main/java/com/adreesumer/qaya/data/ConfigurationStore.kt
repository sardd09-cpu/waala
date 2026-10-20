package com.adreesumer.qaya.data

data class SystemConfiguration(
    val version: String = "0.4.0",
    val isLocalMode: Boolean = true,
    var lastWorkspacePath: String? = null,
    val autoSaveMemory: Boolean = true
)

class ConfigurationStore {
    var currentConfig: SystemConfiguration = SystemConfiguration()
        private set

    fun updateWorkspace(path: String) {
        currentConfig = currentConfig.copy(lastWorkspacePath = path)
        // In reality, persist this to a file like system-settings.json
    }

    fun toggleLocalMode(isLocal: Boolean) {
        currentConfig = currentConfig.copy(isLocalMode = isLocal)
    }
}
