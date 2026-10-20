package com.adreesumer.qaya.skills

import com.adreesumer.qaya.command.QayaCommand
import com.adreesumer.qaya.execution.ExecutionResult
import com.adreesumer.qaya.execution.ExecutionStatus

class SkillsEngine {
    private val skills = mutableListOf<Skill>()

    fun registerSkill(skill: Skill) {
        skills.add(skill)
    }

    fun getSkills(): List<Skill> = skills.toList()

    fun routeCommand(command: QayaCommand): ExecutionResult {
        val handlerSkill = skills.find { it.canHandle(command) }

        return if (handlerSkill != null) {
            try {
                handlerSkill.execute(command)
            } catch (e: Exception) {
                ExecutionResult(
                    status = ExecutionStatus.FAILED,
                    errorMessage = "Skill \${handlerSkill.name} failed: \${e.message}"
                )
            }
        } else {
            ExecutionResult(
                status = ExecutionStatus.FAILED,
                errorMessage = "No skill found to handle command type: \${command.type}"
            )
        }
    }
}
