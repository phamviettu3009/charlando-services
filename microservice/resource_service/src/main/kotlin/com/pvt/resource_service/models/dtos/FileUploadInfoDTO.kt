package com.pvt.resource_service.models.dtos

data class FileUploadInfoDTO(
    var fileName: String,
    val originalFilename: String,
    var fileExtension: String,
    val fileType: Int,
    val directoryPath: String
)
