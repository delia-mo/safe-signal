package com.deliamo.safesignal.data.scanner

import com.deliamo.safesignal.domain.model.RootDetector
import com.deliamo.safesignal.domain.model.RootSignal
import java.io.File

class RealRootDetector : RootDetector {

    override fun detect(): RootSignal {
        val reasons = mutableListOf<String>()

        if (hasTestKeys()) reasons += "System wirkt modifiziert (test-keys)"

        if (hasSuBinary())  reasons += "su-Datei gefunden"

        if (canFindSuWithWhich()) reasons += "su ist im Systempfad auffindbar."

        return RootSignal(
            isRootLikely = reasons.isNotEmpty(),
            reasons = reasons
        )
    }

    private fun hasTestKeys(): Boolean {
        val tags = android.os.Build.TAGS
        return tags != null && tags.contains("test-keys")
    }

    private fun hasSuBinary(): Boolean {
        val paths = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/data/local/bin/su",
            "data/local/xbin/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun canFindSuWithWhich(): Boolean {
        return try {
            val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "which su"))
            val out = p.inputStream.bufferedReader().readLine()
            out != null && out.isNotBlank()
        } catch (_: Throwable) {
            false
        }
    }


}