package com.adreesumer.qaya.planning

import com.adreesumer.qaya.command.QayaCommand

enum class PlanStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class PlanStep(
    val stepId: String = java.util.UUID.randomUUID().toString(),
    val command: QayaCommand,
    var status: PlanStatus = PlanStatus.PENDING,
    var resultMessage: String? = null
)

class QayaPlan(
    val planId: String = java.util.UUID.randomUUID().toString(),
    val steps: List<PlanStep>
) {
    var status: PlanStatus = PlanStatus.PENDING
        private set

    fun updateStatus(newStatus: PlanStatus) {
        this.status = newStatus
    }
}
