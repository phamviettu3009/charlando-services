package com.pvt.channel_service.models.dtos

data class Meta(
    var currentPage: Int,
    val totalPages: Int,
    val sizePerPage: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val last: Boolean
)
