package com.deliamo.safesignal.data.session

import com.deliamo.safesignal.domain.model.ScanFinding
import com.deliamo.safesignal.domain.model.ScanResult
import com.deliamo.safesignal.domain.session.StoredFinding
import com.deliamo.safesignal.domain.session.StoredScan

fun ScanResult.toStoredScan(): StoredScan {
    return StoredScan(
        createdAdMills = System.currentTimeMillis(),
        findings = findings.map { it.toStoredFinding() }
    )
}

fun ScanFinding.toStoredFinding(): StoredFinding {
    return StoredFinding(
        id = id,
        title = title,
        summary = summary,
        severity = severity.name,
        affectedApps  = affectedApps,
        affectedPackages = affectedPackages
    )
}