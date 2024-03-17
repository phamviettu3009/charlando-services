package com.pvt.channel_service.services

import com.pvt.channel_service.models.entitys.AttachmentEntity
import java.util.UUID

interface AttachmentService {
    fun createAttachments(attachmentIDs: List<UUID>?, userID: UUID, messageID: UUID): List<AttachmentEntity>
    fun getAttachments(messageIDs: List<UUID>): List<AttachmentEntity>
}