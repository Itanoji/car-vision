package com.itanoji.carvision.domain.model

data class Inspection(
    val id: Long,
    val inspectionResultId: Long?,
    val title: String,
    val description: String?,
    val avatarMediaId: Long?
)
