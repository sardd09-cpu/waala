package com.adreesumer.qaya

import com.adreesumer.qaya.brain.LocalBrain
import com.adreesumer.qaya.command.CommandEngine
import com.adreesumer.qaya.core.QayaCore
import com.adreesumer.qaya.execution.ExecutionEngine
import com.adreesumer.qaya.memory.MemoryEngine
import com.adreesumer.qaya.planning.PlanEngine
import com.adreesumer.qaya.security.SecurityEngine
import com.adreesumer.qaya.security.SecurityResult
import com.adreesumer.qaya.skills.SkillsEngine
import com.adreesumer.qaya.skills.files.read.ReadSkill
import com.adreesumer.qaya.system.SystemManager
import com.adreesumer.qaya.workspace.WorkspaceEngine
import com.sun.net.httpserver.HttpServer
import java.io.File
import java.net.InetSocketAddress

class QayaFacade {
    private val localBrain: LocalBrain
    private val commandEngine: CommandEngine
    private val planEngine: PlanEngine
    private val securityEngine: SecurityEngine
    private val executionEngine: ExecutionEngine
    private val skillsEngine: SkillsEngine

    init {
        val core = QayaCore()
        val systemManager = SystemManager(core)
        systemManager.bootSystem()

        val memoryEngine = MemoryEngine()
        localBrain = LocalBrain(memoryEngine)

        val workspaceEngine = WorkspaceEngine()
        val currentDir = File(System.getProperty("user.dir"))
        workspaceEngine.bindWorkspace(currentDir.absolutePath)

        // Ensure the test file exists for the user to try
        File(currentDir, "QAYA_TEST.txt").writeText("مرحبا بك في نظام قايا التجريبي عبر الويب! تم قراءة الملف بنجاح من داخل النواة.")

        skillsEngine = SkillsEngine()
        skillsEngine.registerSkill(ReadSkill(workspaceEngine))

        commandEngine = CommandEngine()
        planEngine = PlanEngine()
        securityEngine = SecurityEngine()
        executionEngine = ExecutionEngine()
    }

    fun processInput(input: String): String {
        val brainResult = localBrain.analyzeRequest(input)

        if (brainResult.ambiguityDetected) {
            return brainResult.clarificationNeeded ?: "طلب غير واضح."
        }

        val responses = mutableListOf<String>()

        for (command in brainResult.commands) {
            val securityResult = securityEngine.evaluateCommand(command)
            if (securityResult is SecurityResult.Denied) {
                responses.add("🛡️ الحماية تمنع ذلك: " + securityResult.reason)
                continue
            }

            val plan = planEngine.createPlan(command)

            val result = if (command.type == com.adreesumer.qaya.command.CommandType.READ_FILE) {
                skillsEngine.routeCommand(command)
            } else {
                executionEngine.executePlan(plan)
            }

            responses.add(localBrain.formulateResponse(result))
        }

        return responses.joinToString("\n")
    }
}

fun main() {
    val qaya = QayaFacade()
    val port = 3000
    val server = HttpServer.create(InetSocketAddress(port), 0)

    println("Starting QAYA Web Server on http://localhost:$port")

    server.createContext("/") { exchange ->
        if (exchange.requestMethod == "GET") {
            val html = QayaFacade::class.java.getResource("/index.html")?.readText() ?: "HTML not found"
            val responseBytes = html.toByteArray()
            exchange.responseHeaders.add("Content-Type", "text/html; charset=UTF-8")
            exchange.sendResponseHeaders(200, responseBytes.size.toLong())
            exchange.responseBody.use { it.write(responseBytes) }
        } else {
            exchange.sendResponseHeaders(405, -1)
        }
    }

    server.createContext("/api/command") { exchange ->
        if (exchange.requestMethod == "POST") {
            val input = exchange.requestBody.bufferedReader().use { it.readText() }
            val responseText = qaya.processInput(input)

            val responseBytes = responseText.toByteArray()
            exchange.responseHeaders.add("Content-Type", "text/plain; charset=UTF-8")
            exchange.sendResponseHeaders(200, responseBytes.size.toLong())
            exchange.responseBody.use { it.write(responseBytes) }
        } else {
            exchange.sendResponseHeaders(405, -1)
        }
    }

    server.executor = null
    server.start()
}