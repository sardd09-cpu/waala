package com.adreesumer.qaya.skills.files.read

import com.adreesumer.qaya.command.QayaCommand
import com.adreesumer.qaya.command.CommandType
import com.adreesumer.qaya.execution.ExecutionResult
import com.adreesumer.qaya.execution.ExecutionStatus
import com.adreesumer.qaya.skills.Skill
import com.adreesumer.qaya.workspace.WorkspaceEngine
import java.io.File

class ReadSkill(private val workspaceEngine: WorkspaceEngine) : Skill {
    override val id: String = "files.read"
    override val name: String = "Read File Skill"
    override val description: String = "Reads content from allowed files"

    override fun canHandle(command: QayaCommand): Boolean {
        return command.type == CommandType.READ_FILE
    }

    override fun execute(command: QayaCommand): ExecutionResult {
        val path = command.parameters["path"] as? String
            ?: return ExecutionResult(ExecutionStatus.FAILED, errorMessage = "Path parameter missing")

        val workspace = workspaceEngine.currentWorkspace
            ?: return ExecutionResult(ExecutionStatus.FAILED, errorMessage = "No active workspace")

        val targetFile = workspace.resolvePath(path)

        if (!workspace.isPathWithinWorkspace(targetFile)) {
            return ExecutionResult(ExecutionStatus.PERMISSION_DENIED, errorMessage = "Access denied: Path is outside workspace")
        }

        if (!targetFile.exists() || !targetFile.isFile) {
            return ExecutionResult(ExecutionStatus.FAILED, errorMessage = "File not found or is a directory: \$path")
        }

        return try {
            val content = targetFile.readText()
            ExecutionResult(ExecutionStatus.SUCCESS, data = content)
        } catch (e: Exception) {
            ExecutionResult(ExecutionStatus.FAILED, errorMessage = "Failed to read file: \${e.message}")
        }
    }
}
