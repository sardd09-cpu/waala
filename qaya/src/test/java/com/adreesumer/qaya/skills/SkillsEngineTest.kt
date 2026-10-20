package com.adreesumer.qaya.skills

import com.adreesumer.qaya.command.QayaCommand
import com.adreesumer.qaya.command.CommandType
import com.adreesumer.qaya.execution.ExecutionStatus
import com.adreesumer.qaya.skills.files.read.ReadSkill
import com.adreesumer.qaya.workspace.WorkspaceEngine
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files

class SkillsEngineTest {

    @Test
    fun testReadFileSkill() {
        val tempDir = Files.createTempDirectory("qaya-workspace").toFile()
        val tempFile = File(tempDir, "test.txt")
        tempFile.writeText("Hello Qaya!")

        val workspaceEngine = WorkspaceEngine()
        workspaceEngine.bindWorkspace(tempDir.absolutePath)

        val skillsEngine = SkillsEngine()
        skillsEngine.registerSkill(ReadSkill(workspaceEngine))

        val command = QayaCommand(
            type = CommandType.READ_FILE,
            parameters = mapOf("path" to "test.txt")
        )

        val result = skillsEngine.routeCommand(command)

        assertTrue(result.isSuccess)
        assertEquals("Hello Qaya!", result.data)

        tempFile.delete()
        tempDir.delete()
    }

    @Test
    fun testReadFileOutsideWorkspaceDenied() {
        val tempDir = Files.createTempDirectory("qaya-workspace").toFile()

        val workspaceEngine = WorkspaceEngine()
        workspaceEngine.bindWorkspace(tempDir.absolutePath)

        val skillsEngine = SkillsEngine()
        skillsEngine.registerSkill(ReadSkill(workspaceEngine))

        // Attempt Path Traversal
        val command = QayaCommand(
            type = CommandType.READ_FILE,
            parameters = mapOf("path" to "../outside.txt")
        )

        val result = skillsEngine.routeCommand(command)

        assertFalse(result.isSuccess)
        assertEquals(ExecutionStatus.PERMISSION_DENIED, result.status)

        tempDir.delete()
    }
}
