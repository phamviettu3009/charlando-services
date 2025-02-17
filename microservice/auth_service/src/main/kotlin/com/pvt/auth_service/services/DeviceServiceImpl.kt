package com.pvt.auth_service.services

import com.pvt.auth_service.constants.RabbitMQ
import com.pvt.auth_service.models.dtos.FirebaseDeviceToken
import com.pvt.auth_service.models.dtos.RabbitMessageDTO
import com.pvt.auth_service.models.entitys.asDeviceToken
import com.pvt.auth_service.publisher.RabbitMQProducer
import com.pvt.auth_service.repositories.DeviceRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class DeviceServiceImpl(var deviceRepository: DeviceRepository): DeviceService {
    @Autowired
    lateinit var rabbitMQProducer: RabbitMQProducer

    @RabbitListener(queues = [RabbitMQ.Listener.MSCMN_GET_DEVICE])
    override fun getDevicesByUserIDs(message: RabbitMessageDTO<List<UUID>>) {
        try {
            var userIDs = message.message ?: listOf()
            var devices = deviceRepository.findAllDeviceByUserIDs(ids = userIDs)
            var deviceFirebaseTokens = devices.map { it.asDeviceToken() }

            rabbitMQProducer.sendMessage(deviceFirebaseTokens, RabbitMQ.MSCMN_GET_DEVICE.callbackRoute())
        } catch (e: Exception) {
            println("Error processing message: ${e.message}")
        }
    }

    override fun updateFirebaseToken(firebaseDeviceToken: FirebaseDeviceToken, userID: UUID): String {
        var device = deviceRepository.findByDeviceIDAndUserID(firebaseDeviceToken.deviceID, userID).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        device.firebaseToken = firebaseDeviceToken.firebaseToken
        deviceRepository.saveAndFlush(device)

        return  "Successful"
    }
}