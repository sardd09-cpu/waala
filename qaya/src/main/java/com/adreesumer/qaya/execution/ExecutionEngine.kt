package com.adreesumer.qaya.execution

import com.adreesumer.qaya.planning.QayaPlan
import com.adreesumer.qaya.planning.PlanStatus
import com.adreesumer.qaya.planning.PlanStep
import com.adreesumer.qaya.command.CommandType

class ExecutionEngine {

    fun executePlan(plan: QayaPlan): ExecutionResult {
        plan.updateStatus(PlanStatus.IN_PROGRESS)

        for (step in plan.steps) {
            val result = executeStep(step)

            if (!result.isSuccess) {
                step.status = PlanStatus.FAILED
                step.resultMessage = result.errorMessage
                plan.updateStatus(PlanStatus.FAILED)
                return result
            } else {
                step.status = PlanStatus.COMPLETED
                step.resultMessage = "Success"
            }
        }

        plan.updateStatus(PlanStatus.COMPLETED)
        return ExecutionResult(status = ExecutionStatus.SUCCESS, data = "Plan executed successfully")
    }

    private fun executeStep(step: PlanStep): ExecutionResult {
        val command = step.command

        // Simulating routing to skills based on command type
        return when (command.type) {
            CommandType.SYSTEM_STATUS -> {
                ExecutionResult(status = ExecutionStatus.SUCCESS, data = "System is operational")
            }
            CommandType.READ_FILE -> {
                val path = command.parameters["path"] as? String
                if (path.isNullOrEmpty()) {
                    ExecutionResult(status = ExecutionStatus.FAILED, errorMessage = "Path is missing")
                } else {
                    // Simulate file read success
                    ExecutionResult(status = ExecutionStatus.SUCCESS, data = "Content of $path")
                }
            }
            CommandType.UNKNOWN -> {
                ExecutionResult(status = ExecutionStatus.FAILED, errorMessage = "Unknown command cannot be executed")
            }
            else -> {
                ExecutionResult(status = ExecutionStatus.FAILED, errorMessage = "Skill not implemented for ${command.type}")
            }
        }
    }
}
