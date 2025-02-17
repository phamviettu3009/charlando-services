package com.pvt.channel_service.models.dtos

import java.util.UUID

data class ReplyDTO(
    var id: UUID,
    var type: Int,
    var message: String?,
    var attachments: List<AttachmentDTO>?
)
