package com.adreesumer.qaya.memory

import org.junit.Assert.*
import org.junit.Test

class MemoryEngineTest {

    @Test
    fun testMemoryStorageAndRetrieval() {
        val memoryEngine = MemoryEngine()

        memoryEngine.store(
            type = MemoryType.KNOWLEDGE,
            content = "Project uses Gradle with Kotlin DSL",
            source = "build.gradle.kts",
            entities = listOf("build", "gradle", "project")
        )

        memoryEngine.store(
            type = MemoryType.WORKING,
            content = "Currently reading file config.json"
        )

        val knowledge = memoryEngine.retrieveByType(MemoryType.KNOWLEDGE)
        assertEquals(1, knowledge.size)
        assertEquals("Project uses Gradle with Kotlin DSL", knowledge[0].content)

        val gradleEntities = memoryEngine.retrieveByEntity("gradle")
        assertEquals(1, gradleEntities.size)

        memoryEngine.clearWorkingMemory()
        val working = memoryEngine.retrieveByType(MemoryType.WORKING)
        assertEquals(0, working.size)
    }
}
