package com.adreesumer.qaya.security

import com.adreesumer.qaya.command.QayaCommand
import com.adreesumer.qaya.command.CommandType
import com.adreesumer.qaya.command.CommandRiskLevel
import org.junit.Assert.*
import org.junit.Test

class SecurityEngineTest {

    @Test
    fun testAllowedCommand() {
        val securityEngine = SecurityEngine()
        val command = QayaCommand(type = CommandType.READ_FILE)

        val result = securityEngine.evaluateCommand(command)

        assertTrue(result is SecurityResult.Allowed)
        assertEquals(1, securityEngine.auditLogger.getLogs().size)
    }

    @Test
    fun testDeniedCommand() {
        val securityEngine = SecurityEngine()
        val command = QayaCommand(type = CommandType.DELETE_FILE)

        val result = securityEngine.evaluateCommand(command)

        assertTrue(result is SecurityResult.Denied)
        val denied = result as SecurityResult.Denied
        assertTrue(denied.reason.contains("Permission denied"))
        assertEquals(2, securityEngine.auditLogger.getLogs().size) // Received + Denied
    }
}
