package com.pvt.resource_service.controllers

import com.pvt.resource_service.models.dtos.RequestPayloadDTO
import com.pvt.resource_service.services.DownloadService
import com.pvt.resource_service.utils.asRequestAttribute
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/resource")
class DownloadController {
    @Autowired
    private lateinit var downloadService: DownloadService

    @GetMapping("/download/{id}")
    @ResponseBody
    fun download(request: HttpServletRequest, @PathVariable id: UUID): ResponseEntity<out Any> {
        val requestPayload = RequestPayloadDTO(jwtBody = request.asRequestAttribute(), id)
        val res = downloadService.download(requestPayload).getOrNull()
        return if (res is Resource) {
            ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.CONTENT_DISPOSITION).body<Resource>(res)
        } else {
            ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error when trying to download the requested resource!")
        }
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    fun getResource(
        request: HttpServletRequest,
        @PathVariable id: UUID,
        @RequestParam(defaultValue = "") sizeOption: String
    ): ResponseEntity<out Any> {
        val requestPayload = RequestPayloadDTO(jwtBody = request.asRequestAttribute(), id)
        val res = downloadService.getResource(requestPayload, sizeOption).getOrNull()
        return when(res) {
            is ByteArray -> {
                val headers = HttpHeaders().apply {
                    contentType = MediaType.IMAGE_JPEG
                    contentLength = res.size.toLong()
                }

                return ResponseEntity(res, headers, HttpStatus.OK)
            }
            is FileSystemResource -> {
                val headers = HttpHeaders().apply {
                    contentType = MediaType.parseMediaType("video/mp4")
                    contentLength = res.contentLength()
                }

                return ResponseEntity(res, headers, HttpStatus.OK)
            }
            else -> ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error when trying to get the requested resource!")
        }
    }

    @GetMapping("/get/{id}/thumbnail")
    @ResponseBody
    fun getThumbnail(request: HttpServletRequest, @PathVariable id: UUID): ResponseEntity<ByteArray> {
        val requestPayload = RequestPayloadDTO(jwtBody = request.asRequestAttribute(), id)
        val res = downloadService.getThumbnail(requestPayload).getOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val headers = HttpHeaders().apply {
            contentType = MediaType.IMAGE_JPEG
            contentLength = res.size.toLong()
        }

        return ResponseEntity(res, headers, HttpStatus.OK)
    }
}