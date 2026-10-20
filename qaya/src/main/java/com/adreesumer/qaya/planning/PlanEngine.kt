package com.adreesumer.qaya.planning

import com.adreesumer.qaya.command.QayaCommand

class PlanEngine {

    fun createPlan(command: QayaCommand): QayaPlan {
        // In the future, complex commands will be broken down into multiple steps.
        // For now, a simple command becomes a single-step plan.
        val step = PlanStep(command = command)
        return QayaPlan(steps = listOf(step))
    }

    fun createPlan(commands: List<QayaCommand>): QayaPlan {
        val steps = commands.map { PlanStep(command = it) }
        return QayaPlan(steps = steps)
    }
}
