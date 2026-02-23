package com.deliamo.safesignal.data.session

import com.deliamo.safesignal.domain.model.ScanResult

class SessionService(private val store: SessionStore) {

    suspend fun onScanCompleted(result: ScanResult) {
        val s  = store.load()

        val updated = if (s.baseline == null) {
            s.copy(
                baseline = result.toStoredScan(),
                lastScanAtMillis = System.currentTimeMillis()
            )
        } else {
            s.copy(lastScanAtMillis = System.currentTimeMillis())
        }
        store.save(updated)
    }

    suspend fun markActionStatus(findingId: String, status: String) {
        val s = store.load()
        store.save(s.copy(actionStatus = s.actionStatus + (findingId to status)))
    }

    suspend fun updateResolvedFromRescan(current: ScanResult) {
        val s = store.load()
        val baseline = s.baseline ?: return

        val currentIds = current.findings.map { it.id }.toSet()
        val resolvedIds = baseline.findings
            .map { it.id }
            .filter { it !in currentIds }

        var newMap = s.actionStatus
        resolvedIds.forEach { id ->
            val old = newMap[id]
            if (old != "DONE") newMap = newMap + (id to "DONE")
        }
        store.save(s.copy(actionStatus = newMap, lastScanAtMillis = System.currentTimeMillis()))
    }

    suspend fun hasBaseline(): Boolean = store.load().baseline != null
}