package com.pvt.resource_service.constants

import java.nio.file.Path
import java.nio.file.Paths

object FileConstant {
    object FolderPath {
        val uploadsFolderPathPrivateImage: Path = Paths.get("resources/uploads/private/images")
        val uploadsFolderPathPrivateVideo: Path = Paths.get("resources/uploads/private/videos")
        val uploadsFolderPathPrivateAudio: Path = Paths.get("resources/uploads/private/audios")
        val uploadsFolderPathPrivateThumbnail: Path = Paths.get("resources/uploads/private/thumbnails")
        val uploadsFolderPathPrivateDocument: Path = Paths.get("resources/uploads/private/documents")
        val uploadsFolderPathPublicImage: Path = Paths.get("resources/uploads/public/images")
        val uploadsFolderPathPublicVideo: Path = Paths.get("resources/uploads/public/videos")
        val uploadsFolderPathPublicAudio: Path = Paths.get("resources/uploads/public/audios")
        val uploadsFolderPathPublicThumbnail: Path = Paths.get("resources/uploads/public/thumbnails")
        val uploadsFolderPathPublicDocument: Path = Paths.get("resources/uploads/public/documents")
        val uploadsFolderPathAvatar: Path = Paths.get("resources/uploads/public/avatars")
    }
    object FileType {
        const val IMAGE = 1
        const val VIDEO = 2
        const val AUDIO = 3
        const val DOCUMENT = 4
        const val OTHER = 5
    }
}