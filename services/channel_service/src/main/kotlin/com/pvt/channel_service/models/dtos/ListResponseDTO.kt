package com.pvt.channel_service.models.dtos

data class ListResponseDTO<T>(
    val data: List<T>,
    val meta: Meta
)