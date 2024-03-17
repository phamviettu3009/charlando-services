package com.pvt.resource_service.services

import com.pvt.resource_service.constants.FileConstant
import com.pvt.resource_service.models.dtos.FileUploadInfoDTO
import com.pvt.resource_service.utils.date.DateUtils
import com.pvt.resource_service.utils.file.FileUtils
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.pathString

@Service
class UploadServiceImpl : UploadService {
    private val logger = LogManager.getLogger(UploadServiceImpl::class.java)

    override fun init(): Result<Path> {
        return runCatching {
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathPrivateImage)
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathPrivateVideo)
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathPrivateAudio)
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathPrivateThumbnail)
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathPrivateDocument)
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathPublicImage)
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathPublicVideo)
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathPublicAudio)
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathPublicThumbnail)
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathPublicDocument)
            Files.createDirectories(FileConstant.FolderPath.uploadsFolderPathAvatar)
        }
            .onFailure { logger.error("Error creating resource folder") }
            .onSuccess { logger.info("Created folder resource successfully") }
    }

    @Transactional
    override fun upload(file: MultipartFile, destination: String): Result<FileUploadInfoDTO> {
        return runCatching {
            val originalFilename = file.originalFilename
            val extension = extractFileExtension(originalFilename)
            val fileName = generatorFileName(extension)
            val fileType = FileUtils.getFileType(file)
            val folderPath = extractFolderPathFromEndpoint(destination)

            val uploadedTargetFilePath = folderPath.resolve(fileName)
            Files.copy(file.inputStream, uploadedTargetFilePath)
            uploadedTargetFilePath.isRegularFile()
            FileUploadInfoDTO(fileName, originalFilename!!, extension, fileType, folderPath.pathString)
        }.onFailure {

        }
    }

    @Transactional
    override fun makeThumbnail(fileUploadInfo: FileUploadInfoDTO, destination: String): Result<FileUploadInfoDTO> {
        return runCatching {
            val folderPathVideo = extractFolderPathFromEndpoint(destination)
            val folderPathThumbnail = getFolderPathThumbnailByVideoDestination(destination)
            val videoFilePath = "${folderPathVideo.pathString}/${fileUploadInfo.fileName}"
            val fileThumbnailName = generatorFileName("jpg")

            val thumbnailFilePath = "${folderPathThumbnail.pathString}/${fileThumbnailName}"

            val builder = FFmpegBuilder()
                .setInput(videoFilePath)
                .overrideOutputFiles(true)
                .addOutput(thumbnailFilePath)
                .setFrames(1)
                .done()

            val ffmpeg = FFmpeg("ffmpeg")
            val ffprobe = FFprobe("ffprobe")
            val executor = FFmpegExecutor(ffmpeg, ffprobe)
            executor.createJob(builder).run()
            FileUploadInfoDTO(fileThumbnailName, "", "jpg", FileConstant.FileType.IMAGE, folderPathThumbnail.pathString)
        }
    }

    private fun generatorFileName(extension: String = ""): String {
        val timestamp = DateUtils.getCurrentTimestamp()
        if (extension.isEmpty()) {
            return "resource_${timestamp}"
        }
        return "resource_${timestamp}.${extension}"
    }

    private fun extractFileExtension(originalFilename: String?): String {
        return originalFilename?.substringAfterLast(".") ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
    }

    private fun extractFolderPathFromEndpoint(destination: String): Path {
        return when (destination) {
            "/private/image" -> FileConstant.FolderPath.uploadsFolderPathPrivateImage
            "/public/image" -> FileConstant.FolderPath.uploadsFolderPathPublicImage
            "/private/video" -> FileConstant.FolderPath.uploadsFolderPathPrivateVideo
            "/public/video" -> FileConstant.FolderPath.uploadsFolderPathPublicVideo
            "/private/audio" -> FileConstant.FolderPath.uploadsFolderPathPrivateAudio
            "/public/audio" -> FileConstant.FolderPath.uploadsFolderPathPublicAudio
            "/private/thumbnail" -> FileConstant.FolderPath.uploadsFolderPathPrivateThumbnail
            "/public/thumbnail" -> FileConstant.FolderPath.uploadsFolderPathPublicThumbnail
            "/private/document" -> FileConstant.FolderPath.uploadsFolderPathPrivateDocument
            "/public/document" -> FileConstant.FolderPath.uploadsFolderPathPublicDocument
            "/public/avatar" -> FileConstant.FolderPath.uploadsFolderPathAvatar
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
    }

    private fun getFolderPathThumbnailByVideoDestination(destination: String): Path {
        return when (destination) {
            "/private/video" -> FileConstant.FolderPath.uploadsFolderPathPrivateThumbnail
            "/public/video" -> FileConstant.FolderPath.uploadsFolderPathPublicThumbnail
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
    }
}