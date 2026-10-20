package com.adreesumer.qaya.skills

import com.adreesumer.qaya.command.QayaCommand
import com.adreesumer.qaya.execution.ExecutionResult

interface Skill {
    val id: String
    val name: String
    val description: String

    fun canHandle(command: QayaCommand): Boolean
    fun execute(command: QayaCommand): ExecutionResult
}
