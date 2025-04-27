package com.itanoji.carvision.domain.model

data class Media(
    val id: Long,
    val inspectionId: Long?,
    val resultId: Long?,
    val type: Long?,
    val filename: String
)
