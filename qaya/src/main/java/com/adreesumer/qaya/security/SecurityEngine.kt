package com.adreesumer.qaya.security

import com.adreesumer.qaya.command.QayaCommand
import com.adreesumer.qaya.security.permissions.PermissionManager
import com.adreesumer.qaya.security.confirmation.ConfirmationManager
import com.adreesumer.qaya.security.audit.AuditLogger
import com.adreesumer.qaya.security.audit.AuditEventType

class SecurityEngine(
    val permissionManager: PermissionManager = PermissionManager(),
    val confirmationManager: ConfirmationManager = ConfirmationManager(),
    val auditLogger: AuditLogger = AuditLogger()
) {

    fun evaluateCommand(command: QayaCommand): SecurityResult {
        auditLogger.logEvent(
            AuditEventType.COMMAND_RECEIVED,
            "Evaluating command: \${command.type}",
            "Evaluating command: " + command.type.toString(),
            command.commandId
        )

        // 1. Check Permissions
        if (!permissionManager.isCommandAllowed(command.type)) {
            val msg = "Permission denied for command: \${command.type}"
            val msg = "Permission denied for command: " + command.type.toString()
            auditLogger.logEvent(AuditEventType.PERMISSION_DENIED, msg, command.commandId)
            return SecurityResult.Denied(msg)
        }

        // 2. Check Confirmation
        if (confirmationManager.requiresConfirmation(command.type, command.riskLevel)) {
            val confirmed = confirmationManager.requestConfirmation("Do you allow \${command.type}?")
            if (!confirmed) {
                val msg = "User denied confirmation for: \${command.type}"
            val confirmed = confirmationManager.requestConfirmation("Do you allow " + command.type.toString() + "?")
            if (!confirmed) {
                val msg = "User denied confirmation for: " + command.type.toString()
                auditLogger.logEvent(AuditEventType.PERMISSION_DENIED, msg, command.commandId)
                return SecurityResult.Denied(msg)
            }
        }

        return SecurityResult.Allowed
    }
}

sealed class SecurityResult {
    object Allowed : SecurityResult()
    data class Denied(val reason: String) : SecurityResult()
}
