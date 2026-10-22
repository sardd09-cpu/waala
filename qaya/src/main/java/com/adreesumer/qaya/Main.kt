package com.adreesumer.qaya

import com.adreesumer.qaya.brain.LocalBrain
import com.adreesumer.qaya.brain.Intent
import com.adreesumer.qaya.command.CommandEngine
import com.adreesumer.qaya.core.QayaCore
import com.adreesumer.qaya.execution.ExecutionEngine
import com.adreesumer.qaya.execution.ExecutionStatus
import com.adreesumer.qaya.memory.MemoryEngine
import com.adreesumer.qaya.planning.PlanEngine
import com.adreesumer.qaya.security.SecurityEngine
import com.adreesumer.qaya.security.SecurityResult
import com.adreesumer.qaya.skills.SkillsEngine
import com.adreesumer.qaya.skills.files.read.ReadSkill
import com.adreesumer.qaya.system.SystemManager
import com.adreesumer.qaya.workspace.WorkspaceEngine
import java.io.File

fun main(args: Array<String>) {
    println("==================================================")
    println("        🤖 QAYA SYSTEM INITIALIZATION LAB         ")
    println("==================================================")

    // 1. Initialize Core
    val core = QayaCore()
    val systemManager = SystemManager(core)
    systemManager.bootSystem()
    println("✅ System Boot: ${systemManager.getSystemStatus()}")

    // 2. Setup Memory and Brain
    val memoryEngine = MemoryEngine()
    val localBrain = LocalBrain(memoryEngine)
    println("✅ Local Brain & Memory connected.")

    // 3. Setup Workspace and Skills
    val workspaceEngine = WorkspaceEngine()
    val currentDir = File(System.getProperty("user.dir"))
    workspaceEngine.bindWorkspace(currentDir.absolutePath)

    val skillsEngine = SkillsEngine()
    val readSkill = ReadSkill(workspaceEngine)
    skillsEngine.registerSkill(readSkill)
    println("✅ Workspace bound to: ${currentDir.absolutePath}")
    println("✅ Skills registered: [${readSkill.name}]")

    // 4. Setup Engines (Command, Plan, Security, Execution)
    val commandEngine = CommandEngine()
    val planEngine = PlanEngine()
    val securityEngine = SecurityEngine()
    val executionEngine = ExecutionEngine()
    println("✅ Core Engines (Command, Plan, Security, Execution) ready.")
    println("==================================================\n")

    // --- SIMULATION ---

    // Let's create a test file to read
    val testFile = File(currentDir, "QAYA_TEST.txt")
    testFile.writeText("مرحبا بك في نظام قايا التجريبي! هذا الملف يعمل بنجاح من داخل مساحة العمل.")

    val userInputs = listOf(
        "يا قايا ما هي حالة النظام؟",
        "يا قايا اقرأ ملف QAYA_TEST.txt",
        "اقرأ ملف خارج النطاق ../secret.txt",
        "احذف ملف QAYA_TEST.txt" // Should be denied by default, handled by Intent.QUESTION right now as delete is not parsed
    )

    for (input in userInputs) {
        println("🗣️ User: \"$input\"")

        // 1. Brain Analysis
        val brainResult = localBrain.analyzeRequest(input)
        if (brainResult.ambiguityDetected) {
            println("🧠 Brain: ${brainResult.clarificationNeeded}")
            println("--------------------------------------------------")
            continue
        }

        println("🧠 Brain Intent: ${brainResult.intent}, Commands Generated: ${brainResult.commands.size}")

        for (command in brainResult.commands) {
            println("⚙️ Command: ${command.type} | Parameters: ${command.parameters}")

            // 2. Security Check
            val securityResult = securityEngine.evaluateCommand(command)
            if (securityResult is SecurityResult.Denied) {
                println("🛡️ Security Denied: ${securityResult.reason}")
                continue
            }
            println("🛡️ Security: Allowed.")

            // 3. Planning
            val plan = planEngine.createPlan(command)
            println("📋 Plan created with ${plan.steps.size} steps.")

            // 4. Execution (Routing to skills engine if appropriate, or handling directly)
            // For the lab, we will manually route READ_FILE to the skills engine.
            val result = if (command.type == com.adreesumer.qaya.command.CommandType.READ_FILE) {
                skillsEngine.routeCommand(command)
            } else {
                executionEngine.executePlan(plan)
            }

            // 5. Brain response formulation
            val response = localBrain.formulateResponse(result)

            if (result.isSuccess) {
                println("✅ Execution Success: ${result.data}")
            } else {
                println("❌ Execution Failed: ${result.errorMessage}")
            }
            println("🤖 Qaya: \"$response\"")
        }
        println("--------------------------------------------------")
    }

    // Cleanup
    testFile.delete()

    println("\n📊 AUDIT LOGS:")
    securityEngine.auditLogger.getLogs().forEach { log ->
        println("   [${log.eventType}] ${log.details}")
    }
}