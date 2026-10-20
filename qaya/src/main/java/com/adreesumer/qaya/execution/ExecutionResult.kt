package com.adreesumer.qaya.execution

enum class ExecutionStatus {
    SUCCESS,
    FAILED,
    CANCELLED,
    PERMISSION_DENIED
}

data class ExecutionResult(
    val status: ExecutionStatus,
    val data: Any? = null,
    val errorMessage: String? = null
) {
    val isSuccess: Boolean
        get() = status == ExecutionStatus.SUCCESS
}
