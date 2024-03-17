package com.pvt.resource_service.controllers

import com.pvt.resource_service.constants.FileConstant
import com.pvt.resource_service.constants.RabbitMQ
import com.pvt.resource_service.models.dtos.FileUploadInfoDTO
import com.pvt.resource_service.models.dtos.RecordLevelAccessPayloadDTO
import com.pvt.resource_service.models.dtos.RequestPayloadDTO
import com.pvt.resource_service.models.dtos.ResourceDTO
import com.pvt.resource_service.models.entitys.ResourceEntity
import com.pvt.resource_service.models.entitys.ThumbnailEntity
import com.pvt.resource_service.models.entitys.asDTO
import com.pvt.resource_service.models.entitys.asRecordLevelAccessPayloadDTO
import com.pvt.resource_service.publisher.RabbitMQProducer
import com.pvt.resource_service.repositories.ThumbnailRepository
import com.pvt.resource_service.services.ResourceService
import com.pvt.resource_service.services.UploadService
import com.pvt.resource_service.utils.asRequestAttribute
import com.pvt.resource_service.utils.file.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/resource/upload")
class UploadController {
    @Autowired
    private lateinit var uploadService: UploadService

    @Autowired
    private lateinit var resourceService: ResourceService

    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    @Autowired
    private lateinit var thumbnailRepository: ThumbnailRepository

    @PostMapping("/private/multi")
    fun uploadPrivateMulti(
        request: HttpServletRequest,
        @RequestParam("files") files: List<MultipartFile>
    ): ResponseEntity<Any> {
        var result: MutableList<ResourceDTO> = mutableListOf()
        for (file in files) {
            val fileInfo = extractFileInfo(file, "private")
            val fileType = fileInfo["type"] as Int
            val destination = fileInfo["destination"] as String
            result.add(uploadHandler(file, request, fileType, destination))
        }
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping("/public/multi")
    fun uploadPublicMulti(
        request: HttpServletRequest,
        @RequestParam("files") files: List<MultipartFile>
    ): ResponseEntity<Any> {
        var result: MutableList<ResourceDTO> = mutableListOf()
        for (file in files) {
            val fileInfo = extractFileInfo(file, "public")
            val fileType = fileInfo["type"] as Int
            val destination = fileInfo["destination"] as String
            result.add(uploadHandler(file, request, fileType, destination))
        }
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping("/public/avatar")
    fun uploadPublicAvatar(
        request: HttpServletRequest,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Any> {
        return ResponseEntity(
            uploadHandler(file, request, FileConstant.FileType.IMAGE, "/public/avatar"),
            HttpStatus.OK
        )
    }

    @Transactional
    private fun uploadHandler(
        file: MultipartFile,
        request: HttpServletRequest,
        expectationValidation: Int,
        destination: String
    ): ResourceDTO {
        FileUtils.validation(file, expectationValidation)
        var resource: ResourceDTO? = null

        val uploaded = uploadService.upload(file, destination).onSuccess {
            val requestPayload = RequestPayloadDTO(jwtBody = request.asRequestAttribute(), it)
            val result = resourceService.createResource(requestPayload)
            resource = result.asDTO()
            val recordLevelAccessPayload = result.asRecordLevelAccessPayloadDTO()
            rabbitMQProducer.sendAndCallbackMessage<String>(
                listOf(recordLevelAccessPayload),
                RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.route(),
                RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.callbackQueue()
            )

            if (it.fileType == FileConstant.FileType.VIDEO) {
                makeThumbnail(requestPayload, destination, result.id)
            }
        }

        return if (uploaded.isSuccess) {
            resource!!
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
    }

    @Transactional
    private fun makeThumbnail(requestPayload: RequestPayloadDTO<FileUploadInfoDTO>, destination: String, videoID: UUID) {
        val videoUploadInfo = requestPayload.payload
        uploadService.makeThumbnail(videoUploadInfo, destination).onSuccess { fileThumbnail ->
            val thumbnailRequestPayload = requestPayload.copy(payload = fileThumbnail)
            val resource = resourceService.createResource(thumbnailRequestPayload)
            val thumbnailEntity = ThumbnailEntity(thumbnailID = resource.id, videoID = videoID)
            thumbnailRepository.saveAndFlush(thumbnailEntity)
            val recordLevelAccessPayload = resource.asRecordLevelAccessPayloadDTO(videoID = videoID)
            rabbitMQProducer.sendAndCallbackMessage<String>(
                listOf(recordLevelAccessPayload),
                RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.route(),
                RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.callbackQueue()
            )
        }
    }

    private fun extractFileInfo(file: MultipartFile, accessLevel: String): MutableMap<String, Any> {
        var result = mutableMapOf<String, Any>()
        val fileType = file.contentType ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (fileType.contains("image")) {
            result["type"] = FileConstant.FileType.IMAGE
            result["destination"] = "/$accessLevel/image"
        }

        if (fileType.contains("video")) {
            result["type"] = FileConstant.FileType.VIDEO
            result["destination"] = "/$accessLevel/video"
        }

        if (fileType.contains("audio")) {
            result["type"] = FileConstant.FileType.AUDIO
            result["destination"] = "/$accessLevel/audio"
        }

        if (fileType.contains("document")) {
            result["type"] = FileConstant.FileType.DOCUMENT
            result["destination"] = "/$accessLevel/document"
        }

        if (result["type"] == null || result["destination"] == null) {
            println("vo")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        return result
    }
}