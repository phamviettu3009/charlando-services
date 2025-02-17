package com.pvt.channel_service.models.dtos

data class ListRequestDTO(
    val page: Int,
    val sizePerPage: Int,
    val sortBy: String,
    val keyword: String
)
