package com.pvt.channel_service.services

import com.pvt.channel_service.constants.RabbitMQ
import com.pvt.channel_service.models.entitys.AttachmentEntity
import com.pvt.channel_service.publisher.RabbitMQProducer
import com.pvt.channel_service.repositories.AttachmentRepository
import com.pvt.channel_service.utils.extension.asUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class AttachmentServiceImpl : AttachmentService {
    @Autowired
    private lateinit var attachmentRepository: AttachmentRepository

    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    private fun getResources(attachmentIDs: List<UUID>?, userID: UUID): ResourceList {
        if (attachmentIDs == null) return emptyList()
        val response = rabbitMQProducer.sendAndCallbackMessage<ResourceList>(
            mapOf("attachmentIDs" to attachmentIDs, "userID" to userID),
            RabbitMQ.MSCMN_GET_RESOURCE_BY_ID_AND_USERID.route(),
            RabbitMQ.MSCMN_GET_RESOURCE_BY_ID_AND_USERID.callbackQueue()
        )

        return response.message ?: emptyList()
    }

    override fun createAttachments(attachmentIDs: List<UUID>?, userID: UUID, messageID: UUID): List<AttachmentEntity> {
        val resources = getResources(attachmentIDs, userID)
        if (resources.isEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (resources.size != attachmentIDs?.size) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val attachments = resources.map {
            AttachmentEntity(
                id = (it["id"] as String).asUUID(),
                messageID = messageID,
                type = it["type"] as Int,
                makerID = userID
            )
        }
        return attachmentRepository.saveAllAndFlush(attachments)
    }

    override fun getAttachments(messageIDs: List<UUID>): List<AttachmentEntity> {
        return attachmentRepository.findAllByMessageIDs(messageIDs)
    }
}

private typealias ResourceList = List<Map<String, Any>>