package com.pvt.resource_service.services

import com.pvt.resource_service.constants.RabbitMQ
import com.pvt.resource_service.models.dtos.FileUploadInfoDTO
import com.pvt.resource_service.models.dtos.RabbitMessageDTO
import com.pvt.resource_service.models.dtos.RequestPayloadDTO
import com.pvt.resource_service.models.entitys.ResourceEntity
import com.pvt.resource_service.publisher.RabbitMQProducer
import com.pvt.resource_service.repositories.ResourceRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ResourceServiceImpl(
    val resourceRepository: ResourceRepository,
    val rabbitMQProducer: RabbitMQProducer
): ResourceService {

    @Transactional
    override fun createResource(requestPayload: RequestPayloadDTO<FileUploadInfoDTO>): ResourceEntity {
        val fileUpload: FileUploadInfoDTO = requestPayload.payload
        val jwtBody = requestPayload.jwtBody
        val resource = ResourceEntity(
            name = fileUpload.fileName,
            extension = fileUpload.fileExtension,
            type = fileUpload.fileType,
            makerID = jwtBody.userID,
            originalName = fileUpload.originalFilename,
            directoryPath = fileUpload.directoryPath
        )

        return resourceRepository.saveAndFlush(resource)
    }

    @RabbitListener(queues = [RabbitMQ.Listener.MSCMN_GET_RESOURCE_BY_ID_AND_USERID])
    private fun createRecordLevelAccess(data: RabbitMessageDTO<Map<String, Any>>) {
        try {
            val attachmentIDs = data.message?.get("attachmentIDs") as List<String>
            val userID = UUID.fromString(data.message["userID"] as String)
            val resources = resourceRepository.findAllByIDsAndUserID(attachmentIDs.map { UUID.fromString(it) }, userID)
            rabbitMQProducer.sendMessage(resources, RabbitMQ.MSCMN_GET_RESOURCE_BY_ID_AND_USERID.callbackRoute())
        } catch (e: Exception) {
            println("Error processing message: ${e.message}")
        }
    }
}