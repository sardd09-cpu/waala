package com.adreesumer.qaya.security.permissions

import com.adreesumer.qaya.command.CommandType

class PermissionManager {

    // In a real app, this would be tied to user settings and workspace bounds
    private val allowedCommands = mutableSetOf<CommandType>(
        CommandType.SYSTEM_STATUS,
        CommandType.READ_FILE,
        CommandType.LIST_FILES
    )

    fun isCommandAllowed(type: CommandType): Boolean {
        return allowedCommands.contains(type)
    }

    fun grantPermission(type: CommandType) {
        allowedCommands.add(type)
    }

    fun revokePermission(type: CommandType) {
        allowedCommands.remove(type)
    }

    fun isPathAllowed(path: String, workspaceRoot: String): Boolean {
        // Prevent path traversal
        val normalizedPath = java.io.File(path).canonicalPath
        val normalizedWorkspace = java.io.File(workspaceRoot).canonicalPath

        return normalizedPath.startsWith(normalizedWorkspace)
    }
}
