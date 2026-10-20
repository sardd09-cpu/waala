package com.adreesumer.qaya.command

class CommandEngine {

    fun parseCommand(input: String): QayaCommand {
        val parts = input.trim().split("\\s+".toRegex())
        if (parts.isEmpty()) {
            return QayaCommand(type = CommandType.UNKNOWN)
        }

        val action = parts[0].lowercase()

        return when (action) {
            "read" -> {
                val path = parts.getOrNull(1) ?: ""
                QayaCommand(
                    type = CommandType.READ_FILE,
                    parameters = mapOf("path" to path),
                    riskLevel = CommandRiskLevel.LOW
                )
            }
            "status" -> {
                QayaCommand(
                    type = CommandType.SYSTEM_STATUS,
                    riskLevel = CommandRiskLevel.LOW
                )
            }
            // Will be expanded as needed
            else -> QayaCommand(type = CommandType.UNKNOWN)
        }
    }

    fun validate(command: QayaCommand): Boolean {
        return command.isValid()
    }
}
