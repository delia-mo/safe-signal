package com.deliamo.safesignal.domain.session

import kotlinx.serialization.Serializable

@Serializable
data class StoredFinding(
    val id: String,
    val title: String,
    val summary: String,
    val severity: String,
    val affectedApps: List<String> = emptyList(),
    val affectedPackages: List<String> = emptyList()
)

@Serializable
data class StoredScan(
    val createdAdMills: Long,
    val findings: List<StoredFinding>
)

@Serializable
data class StoredSession(
    val baseline: StoredScan? = null,
    val actionStatus: Map<String, String> = emptyMap(),
    val lastScanAtMillis: Long? = null
)