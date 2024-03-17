package com.pvt.auth_service.models.dtos

data class RecordLevelAccessShouldBeChecked(
    val path: String,
    val method: String
)
