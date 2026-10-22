package com.adreesumer.qaya.brain

import com.adreesumer.qaya.command.QayaCommand
import com.adreesumer.qaya.command.CommandType
import com.adreesumer.qaya.command.CommandRiskLevel
import com.adreesumer.qaya.memory.MemoryEngine
import com.adreesumer.qaya.memory.MemoryType

enum class Intent {
    QUESTION,
    ACTION,
    UNKNOWN
}

data class BrainAnalysisResult(
    val intent: Intent,
    val commands: List<QayaCommand>,
    val ambiguityDetected: Boolean = false,
    val clarificationNeeded: String? = null
)

class LocalBrain(private val memoryEngine: MemoryEngine) {

    fun analyzeRequest(userInput: String): BrainAnalysisResult {
        // Save the request to conversation memory
        memoryEngine.store(MemoryType.CONVERSATION, "User: \$userInput")
        memoryEngine.store(MemoryType.CONVERSATION, "User: " + userInput)

        val input = userInput.lowercase()

        // Simple Intent Recognition & Entity Extraction for demonstration
        // "اقرأ ملف README" -> ACTION, READ_FILE
        if (input.contains("اقرأ") || input.contains("read")) {
            val path = extractPath(userInput)
            if (path.isEmpty()) {
                return BrainAnalysisResult(
                    intent = Intent.ACTION,
                    commands = emptyList(),
                    ambiguityDetected = true,
                    clarificationNeeded = "عذراً، لم أتمكن من تحديد اسم الملف. هل يمكنك توضيح المسار؟"
                )
            }

            val command = QayaCommand(
                type = CommandType.READ_FILE,
                parameters = mapOf("path" to path)
            )
            return BrainAnalysisResult(Intent.ACTION, listOf(command))
        }

        if (input.contains("حالة") || input.contains("status")) {
            return BrainAnalysisResult(
                intent = Intent.ACTION,
                commands = listOf(QayaCommand(type = CommandType.SYSTEM_STATUS))
            )
        }

        // Default to question if no action verb is found
        return BrainAnalysisResult(
            intent = Intent.QUESTION,
            commands = emptyList(),
            ambiguityDetected = true,
            clarificationNeeded = "لست متأكداً مما إذا كان هذا أمراً أو سؤالاً، هل يمكنك التوضيح؟"
        )
    }

    private fun extractPath(input: String): String {
        // Very basic extraction: looking for words with dots or typical path structures
        val words = input.split(" ")
        for (word in words) {
            if (word.contains(".") || word.contains("/")) {
                return word
            }
        }

        // Fallback rule for simple demo "read config" -> "config"
        val parts = input.split(" ")
        val index = parts.indexOfFirst { it.lowercase() == "read" || it == "اقرأ" }
        if (index != -1 && index + 1 < parts.size) {
            val target = parts[index + 1]
            if (target != "ملف" && target != "file") {
                return target
            } else if (index + 2 < parts.size) {
                return parts[index + 2]
            }
        }
        return ""
    }

    fun formulateResponse(result: com.adreesumer.qaya.execution.ExecutionResult): String {
        return if (result.isSuccess) {
            "تمت العملية بنجاح. النتيجة: \${result.data}"
        } else {
            "فشلت العملية. السبب: \${result.errorMessage}"
            "تمت العملية بنجاح. النتيجة: " + result.data.toString()
        } else {
            "فشلت العملية. السبب: " + result.errorMessage.toString()
        }
    }
}
