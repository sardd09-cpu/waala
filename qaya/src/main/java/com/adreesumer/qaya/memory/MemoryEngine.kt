package com.adreesumer.qaya.memory

enum class MemoryType {
    WORKING,     // Short term context for current task
    CONVERSATION,// Context for sequential commands
    TASK,        // Historical tasks results
    KNOWLEDGE    // Long term facts and project specifics
}

data class MemoryEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: MemoryType,
    val content: String,
    val source: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val relatedEntities: List<String> = emptyList()
)

class MemoryEngine {
    private val memories = mutableListOf<MemoryEntry>()

    fun store(type: MemoryType, content: String, source: String? = null, entities: List<String> = emptyList()) {
        memories.add(
            MemoryEntry(
                type = type,
                content = content,
                source = source,
                relatedEntities = entities
            )
        )
    }

    fun retrieveByType(type: MemoryType): List<MemoryEntry> {
        return memories.filter { it.type == type }
    }

    fun retrieveByEntity(entity: String): List<MemoryEntry> {
        return memories.filter { it.relatedEntities.contains(entity) }
    }

    fun clearWorkingMemory() {
        memories.removeAll { it.type == MemoryType.WORKING }
    }

    fun summarizeContext(): String {
        val recentConversations = retrieveByType(MemoryType.CONVERSATION)
            .sortedBy { it.timestamp }
            .takeLast(5)

        return recentConversations.joinToString("\n") { it.content }
    }
}
