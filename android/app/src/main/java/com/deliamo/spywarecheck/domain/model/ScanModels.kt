package com.deliamo.spywarecheck.domain.model

enum class Severity { LOW, MEDIUM, HIGH }

data class ScanFinding(
    val id: String,
    val title: String,
    val summary: String,
    val severity: Severity,
    val affectedApps: List<String> = emptyList()
)

data class ScanResult(
    val timestampMills: Long,
    val findings: List<ScanFinding>
)

data class AppRef(
    val packageName: String,
    val label: String,
    val isSystem: Boolean
)