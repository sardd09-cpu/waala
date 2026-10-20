package com.adreesumer.qaya.security.confirmation

import com.adreesumer.qaya.command.CommandType
import com.adreesumer.qaya.command.CommandRiskLevel

class ConfirmationManager {

    fun requiresConfirmation(type: CommandType, riskLevel: CommandRiskLevel): Boolean {
        if (riskLevel == CommandRiskLevel.HIGH) return true

        return when (type) {
            CommandType.DELETE_FILE,
            CommandType.WRITE_FILE -> true
            else -> false
        }
    }

    // Simulating user confirmation
    fun requestConfirmation(prompt: String): Boolean {
        // In reality, this would emit an event to the UI and wait for user response
        println("CONFIRMATION REQUIRED: \$prompt")
        // Defaulting to true for automated tests, should be interactive in UI
        return true
    }
}
