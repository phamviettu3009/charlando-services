package com.pvt.resource_service.services

import com.pvt.resource_service.models.dtos.RequestPayloadDTO
import org.springframework.core.io.Resource
import java.util.*

interface DownloadService {
    fun download(requestPayload: RequestPayloadDTO<UUID>): Result<Resource>
    fun getResource(requestPayload: RequestPayloadDTO<UUID>, sizeOption: String): Result<Any>
    fun getThumbnail(requestPayload: RequestPayloadDTO<UUID>): Result<ByteArray>
}