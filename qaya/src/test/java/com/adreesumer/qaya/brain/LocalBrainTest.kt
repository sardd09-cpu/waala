package com.adreesumer.qaya.brain

import com.adreesumer.qaya.command.CommandType
import com.adreesumer.qaya.memory.MemoryEngine
import org.junit.Assert.*
import org.junit.Test

class LocalBrainTest {

    @Test
    fun testIntentRecognitionEnglish() {
        val brain = LocalBrain(MemoryEngine())

        val result = brain.analyzeRequest("Read file config.json")
        assertEquals(Intent.ACTION, result.intent)
        assertEquals(1, result.commands.size)
        assertEquals(CommandType.READ_FILE, result.commands[0].type)
        assertEquals("config.json", result.commands[0].parameters["path"])
    }

    @Test
    fun testIntentRecognitionArabic() {
        val brain = LocalBrain(MemoryEngine())

        val result = brain.analyzeRequest("يا قايا اقرأ ملف README.md")
        assertEquals(Intent.ACTION, result.intent)
        assertEquals(1, result.commands.size)
        assertEquals(CommandType.READ_FILE, result.commands[0].type)
        assertEquals("README.md", result.commands[0].parameters["path"])
    }

    @Test
    fun testAmbiguityDetection() {
        val brain = LocalBrain(MemoryEngine())

        // Missing file name
        val result = brain.analyzeRequest("اقرأ")

        assertTrue(result.ambiguityDetected)
        assertNotNull(result.clarificationNeeded)
        assertEquals(Intent.ACTION, result.intent)
        assertTrue(result.commands.isEmpty())
    }
}
