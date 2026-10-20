package com.adreesumer.qaya.security.audit

import com.adreesumer.qaya.command.QayaCommand
import com.adreesumer.qaya.execution.ExecutionResult

enum class AuditEventType {
    COMMAND_RECEIVED,
    PERMISSION_DENIED,
    EXECUTION_SUCCESS,
    EXECUTION_FAILED,
    SECURITY_ALERT
}

data class AuditLogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: AuditEventType,
    val details: String,
    val commandId: String? = null
)

class AuditLogger {
    private val logs = mutableListOf<AuditLogEntry>()

    fun logEvent(eventType: AuditEventType, details: String, commandId: String? = null) {
        val entry = AuditLogEntry(
            eventType = eventType,
            details = details,
            commandId = commandId
        )
        logs.add(entry)

        // In the future: write to permanent storage (e.g., SQLite or File)
        println("[AUDIT] [" + entry.eventType.toString() + "] " + entry.details)
    }

    fun getLogs(): List<AuditLogEntry> {
        return logs.toList()
    }

    fun clearLogs() {
        logs.clear()
    }
}
