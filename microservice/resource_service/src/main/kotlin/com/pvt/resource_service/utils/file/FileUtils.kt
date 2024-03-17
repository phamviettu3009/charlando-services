package com.pvt.resource_service.utils.file

import com.pvt.resource_service.constants.FileConstant
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.util.*

object FileUtils {
    fun getFileType(file: MultipartFile): Int {
        try {
            val originalFilename = file.originalFilename
            val fileExtension = originalFilename!!.substring(originalFilename.lastIndexOf(".") + 1).lowercase(Locale.getDefault())
            return when (fileExtension) {
                "jpg", "jpeg", "png", "gif", "bmp", "tiff", "tif", "ico", "webp", "svg", "heif", "heic", "raw" -> FileConstant.FileType.IMAGE
                "mp4", "mov" -> FileConstant.FileType.VIDEO
                "mp3", "m4a" -> FileConstant.FileType.AUDIO
                "doc", "docx", "pdf", "txt" -> FileConstant.FileType.DOCUMENT
                else -> FileConstant.FileType.OTHER
            }
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown file!")
        }
    }

    fun validation(file: MultipartFile, expectation: Int) {
        val fileType = getFileType(file)
        if (fileType != expectation) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect file format!")
        }
    }
}