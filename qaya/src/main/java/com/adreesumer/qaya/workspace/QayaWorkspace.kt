package com.adreesumer.qaya.workspace

import java.io.File

data class QayaWorkspace(
    val rootPath: String,
    val isActive: Boolean = true
) {
    fun resolvePath(relativePath: String): File {
        val root = File(rootPath)
        val target = File(root, relativePath)
        return target
    }

    fun isPathWithinWorkspace(file: File): Boolean {
        return file.canonicalPath.startsWith(File(rootPath).canonicalPath)
    }
}

class WorkspaceEngine {
    var currentWorkspace: QayaWorkspace? = null
        private set

    fun bindWorkspace(path: String): Boolean {
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        if (dir.exists() && dir.isDirectory) {
            currentWorkspace = QayaWorkspace(rootPath = dir.canonicalPath)
            return true
        }
        return false
    }
}
