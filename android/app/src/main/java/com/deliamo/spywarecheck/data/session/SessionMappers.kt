package com.deliamo.spywarecheck.data.session

import com.deliamo.spywarecheck.domain.model.ScanFinding
import com.deliamo.spywarecheck.domain.model.ScanResult
import com.deliamo.spywarecheck.domain.session.StoredFinding
import com.deliamo.spywarecheck.domain.session.StoredScan

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