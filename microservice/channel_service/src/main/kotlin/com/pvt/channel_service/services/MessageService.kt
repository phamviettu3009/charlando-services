package com.pvt.channel_service.services

import com.pvt.channel_service.models.dtos.*
import java.util.UUID

interface MessageService {
    fun createMessage(request: RequestDTO<MessageRequestDTO>): ResponseMessageDTO
    fun updateMessage(request: RequestDTO<MessageUpdateRequestDTO>): ResponseMessageDTO
    fun getMessages(request: RequestDTO<ListRequestDTO>): ListResponseDTO<ResponseMessageDTO>
    fun deleteMessage(request: RequestDTO<String>): ResponseMessageDTO
    fun readMessage(request: RequestDTO<Unit>): MutableList<AvatarDTO>
}