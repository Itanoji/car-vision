package com.itanoji.carvision.domain.model

import java.util.Date
import java.util.UUID

data class InspectionResult(
    val id: Long,
    val inspectionId: Long,
    val licensePlate: String?,
    val color: String?,
    val brand: String?,
    val date: Date?,
    val timeSeconds: Int?
)
