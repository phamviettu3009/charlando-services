package com.pvt.resource_service.models.dtos

import java.util.UUID

data class ResourceDTO(
    var id: UUID,
    var name: String,
    var type: Int
)
