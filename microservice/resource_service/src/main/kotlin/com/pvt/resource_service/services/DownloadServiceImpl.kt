package com.pvt.resource_service.services

import com.pvt.resource_service.constants.AuthStatus
import com.pvt.resource_service.constants.FileConstant
import com.pvt.resource_service.models.dtos.RequestPayloadDTO
import com.pvt.resource_service.repositories.ResourceRepository
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.io.*
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import javax.imageio.ImageIO

@Service
class DownloadServiceImpl: DownloadService {
    @Autowired
    private lateinit var resourceRepository: ResourceRepository

    @Transactional
    override fun download(requestPayload: RequestPayloadDTO<UUID>): Result<Resource> {
        return runCatching {
            val resourceID = requestPayload.payload
            val resourceData = resourceRepository.findByIdAndAuthStatus(id = resourceID, AuthStatus.ACTIVE).orElseThrow {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found!")
            }

            val folderPath = Paths.get(resourceData.directoryPath)
            val file: Path = folderPath.resolve(resourceData.name)
            val resource: Resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                resource
            }

            else throw FileNotFoundException("The file does not exist or is not readable!")
        }
    }

    override fun getResource(requestPayload: RequestPayloadDTO<UUID>, sizeOption: String): Result<Any> {
        return runCatching {
            val resourceID = requestPayload.payload
            val resourceData = resourceRepository.findByIdAndAuthStatus(id = resourceID, AuthStatus.ACTIVE).orElseThrow {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found!")
            }
            val path = "${resourceData.directoryPath}/${resourceData.name}"
            val file = File(path)
            when(resourceData.type) {
                FileConstant.FileType.IMAGE -> {
                    resizeImage(FileInputStream(file).readBytes(), file.extension, sizeOption)
                }
                FileConstant.FileType.VIDEO, FileConstant.FileType.AUDIO -> {
                    FileSystemResource(file)
                }
                else -> throw FileNotFoundException("The file does not exist or is not readable!")
            }
        }
    }

    override fun getThumbnail(requestPayload: RequestPayloadDTO<UUID>): Result<ByteArray> {
        return runCatching {
            val resourceID = requestPayload.payload
            val resourceData = resourceRepository.findThumbnailByVideoID(videoID = resourceID).orElseThrow {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found!")
            }
            val path = "${resourceData.directoryPath}/${resourceData.name}"
            val file = File(path)
            FileInputStream(file).readBytes()
        }
    }

    private fun sizeOptions(sizeOption: String?): Map<String, Int>? {
        return when(sizeOption) {
            "mobile-avatar" ->  mapOf("width" to 200, "height" to 200)
            "mobile-image" -> mapOf("width" to 1000, "height" to 1000)
            else -> null
        }
    }

    private fun resizeImage(byteArray: ByteArray, extension: String, sizeOption: String): ByteArray {
        val size = sizeOptions(sizeOption) ?: return byteArray
        val width = size["width"] as Int
        val height = size["height"] as Int

        val originalImage = ImageIO.read(ByteArrayInputStream(byteArray))
        val originalWidth = originalImage.width
        val originalHeight = originalImage.height
        val scaleFactor = minOf(width.toDouble() / originalWidth, height.toDouble() / originalHeight)

        val resizedImage = Thumbnails.of(ByteArrayInputStream(byteArray))
            .scale(scaleFactor)
            .outputQuality(1.0)
            .asBufferedImage()

        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(resizedImage, extension, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}