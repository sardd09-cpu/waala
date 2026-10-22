package com.adreesumer.qaya

import com.adreesumer.qaya.command.CommandEngine
import com.adreesumer.qaya.command.CommandType
import com.adreesumer.qaya.planning.PlanEngine
import com.adreesumer.qaya.planning.PlanStatus
import com.adreesumer.qaya.execution.ExecutionEngine
import com.adreesumer.qaya.execution.ExecutionStatus
import org.junit.Assert.*
import org.junit.Test

class CommandExecutionLoopTest {

    @Test
    fun testCompleteCommandExecutionLoop() {
        val commandEngine = CommandEngine()
        val planEngine = PlanEngine()
        val executionEngine = ExecutionEngine()

        // 1. User inputs a command
        val userInput = "read /workspace/config.json"

        // 2. Parse command
        val command = commandEngine.parseCommand(userInput)
        assertEquals(CommandType.READ_FILE, command.type)
        assertEquals("/workspace/config.json", command.parameters["path"])
        assertTrue(commandEngine.validate(command))

        // 3. Create plan
        val plan = planEngine.createPlan(command)
        assertEquals(1, plan.steps.size)
        assertEquals(PlanStatus.PENDING, plan.status)

        // 4. Execute plan
        val result = executionEngine.executePlan(plan)

        // 5. Verify results
        assertTrue(result.isSuccess)
        assertEquals(ExecutionStatus.SUCCESS, result.status)
        assertEquals("Content of /workspace/config.json", result.data)
        assertEquals(PlanStatus.COMPLETED, plan.status)
        assertEquals(PlanStatus.COMPLETED, plan.steps[0].status)
    }

    @Test
    fun testInvalidCommandFails() {
        val commandEngine = CommandEngine()
        val planEngine = PlanEngine()
        val executionEngine = ExecutionEngine()

        // Invalid input (empty path for read command handled by parse failure or execution failure)
        val command = commandEngine.parseCommand("read")

        // Let's assume validation fails because path is empty
        assertFalse(commandEngine.validate(command))

        // Even if we force a plan, execution should fail
        val plan = planEngine.createPlan(command)
        val result = executionEngine.executePlan(plan)

        assertFalse(result.isSuccess)
        assertEquals(ExecutionStatus.FAILED, result.status)
        assertEquals(PlanStatus.FAILED, plan.status)
        val command = commandEngine.parseCommand("read") // empty path

        assertFalse("Command should be invalid due to missing path", commandEngine.validate(command))
    }
}
