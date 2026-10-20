package com.adreesumer.qaya.command

enum class CommandType {
    READ_FILE,
    LIST_FILES,
    SEARCH_FILES,
    WRITE_FILE,
    DELETE_FILE,
    SYSTEM_STATUS,
    UNKNOWN
}

enum class CommandRiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

data class QayaCommand(
    val commandId: String = java.util.UUID.randomUUID().toString(),
    val type: CommandType,
    val parameters: Map<String, Any> = emptyMap(),
    val riskLevel: CommandRiskLevel = CommandRiskLevel.LOW
) {
    fun isValid(): Boolean {
        if (type == CommandType.UNKNOWN) return false

        // Basic validation based on type
        return when (type) {
            CommandType.READ_FILE -> parameters.containsKey("path")
            CommandType.WRITE_FILE -> parameters.containsKey("path") && parameters.containsKey("content")
            CommandType.DELETE_FILE -> parameters.containsKey("path")
            else -> true
        }
    }
}
