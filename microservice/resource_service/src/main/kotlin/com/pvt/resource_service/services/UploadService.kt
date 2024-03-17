package com.pvt.resource_service.services

import com.pvt.resource_service.models.dtos.FileUploadInfoDTO
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface UploadService {
    fun init() : Result<Path>
    fun upload(file: MultipartFile, destination: String) : Result<FileUploadInfoDTO>
    fun makeThumbnail(fileUploadInfo: FileUploadInfoDTO, destination: String) : Result<FileUploadInfoDTO>
}