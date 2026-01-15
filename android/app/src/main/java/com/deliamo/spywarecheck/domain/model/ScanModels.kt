package com.deliamo.spywarecheck.domain.model

enum class Severity { LOW, MEDIUM, HIGH }

data class ScanFinding(
    val id: String,
    val title: String,
    val summary: String,
    val severity: Severity,
    val affectedApps: List<String> = emptyList(),
    val affectedPackages: List<String> = emptyList(),
    val details: List<String> = emptyList(),
    val adminEntries: List<AdminEntry> = emptyList()
)

data class AdminEntry(
    val receiverLabel: String,
    val adminPackage: String,
    val componentFlattened: String
)

data class ScanResult(
    val timestampMills: Long,
    val findings: List<ScanFinding>
)

data class AppRef(
    val packageName: String,
    val appLabel: String? = null,
    val receiverLabel: String? = null,
    val isSystem: Boolean
) {
    fun displayName(): String {
        return when {
            !receiverLabel.isNullOrBlank() && !appLabel.isNullOrBlank() && receiverLabel != appLabel ->
                "$receiverLabel ($appLabel)"
            !receiverLabel.isNullOrBlank() -> "$receiverLabel ($packageName)"
            !appLabel.isNullOrBlank() -> "$appLabel ($packageName)"
            else -> packageName
        }
    }
}

data class RootSignal(
    val isRootLikely: Boolean,
    val reasons: List<String>
)

interface RootDetector {
    fun detect(): RootSignal
}