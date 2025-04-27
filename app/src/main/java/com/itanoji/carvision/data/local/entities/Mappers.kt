package com.itanoji.carvision.data.local.entities

import com.itanoji.carvision.domain.model.Inspection
import com.itanoji.carvision.domain.model.InspectionResult
import com.itanoji.carvision.domain.model.Media

fun InspectionEntity.toDomain() = Inspection(
    id = id,
    inspectionResultId = inspectionResultId,
    title = title,
    description = description,
    avatarMediaId = avatarMediaId
)

fun Inspection.toEntity() = InspectionEntity(
    id                 = id,
    inspectionResultId = inspectionResultId,
    title              = title,
    description        = description,
    avatarMediaId      = avatarMediaId
)

fun InspectionResultEntity.toDomain() = InspectionResult(
    id = id,
    inspectionId = inspectionId,
    licensePlate = licensePlate,
    color = color,
    brand = brand,
    date = inspectionDate,
    timeSeconds = inspectionTime
)

fun InspectionResult.toEntity() = InspectionResultEntity(
    id = id,
    inspectionId = inspectionId,
    licensePlate = licensePlate,
    color = color,
    brand = brand,
    inspectionDate = date,
    inspectionTime = timeSeconds
)

fun Media.toEntity() = MediaEntity(
    id            = id,
    inspectionId  = inspectionId,
    resultId      = resultId,
    mediaTypeId   = type,
    filename      = filename
)

fun MediaEntity.toDomain() = Media(
    id            = id,
    inspectionId  = inspectionId,
    resultId      = resultId,
    type          = mediaTypeId,
    filename      = filename
)