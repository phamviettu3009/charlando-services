package com.pvt.resource_service.services

import com.pvt.resource_service.models.dtos.FileUploadInfoDTO
import com.pvt.resource_service.models.dtos.RequestPayloadDTO
import com.pvt.resource_service.models.entitys.ResourceEntity

interface ResourceService {
    fun createResource(requestPayload: RequestPayloadDTO<FileUploadInfoDTO>): ResourceEntity
}