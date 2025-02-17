package com.pvt.auth_service.services

import com.pvt.auth_service.constants.RabbitMQ
import com.pvt.auth_service.models.dtos.RabbitMessageDTO
import com.pvt.auth_service.models.entitys.UserEntity
import com.pvt.auth_service.models.entitys.asUserDTO
import com.pvt.auth_service.publisher.RabbitMQProducer
import com.pvt.auth_service.repositories.UserRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserServiceImpl(val userRepository: UserRepository): UserService {
    @Autowired
    lateinit var rabbitMQProducer: RabbitMQProducer

    @Transactional
    override fun createUser(user: UserEntity): UserEntity {
        return userRepository.saveAndFlush(
            UserEntity(id = UUID.randomUUID(), fullName = user.fullName)
        )
    }

    @Transactional
    override fun putUserMessageToRabbit(id: UUID, routing: String, callBackQueue: String) {
        val user = userRepository.findById(id).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        val response = rabbitMQProducer.sendAndCallbackMessage<String>(user.asUserDTO(), routing, callBackQueue)
        if (response.message == "Compensation") {
            throw Exception("Rollback event")
        }
    }

    @RabbitListener(queues = [RabbitMQ.Listener.MSCMN_GET_USER_BY_ID])
    private fun getUserByID(message: RabbitMessageDTO<UUID>) {
        try {
            val id = message.message!!
            val user = userRepository.findById(id).orElse(null)
            if (user != null) {
                rabbitMQProducer.sendMessage(user, RabbitMQ.MSCMN_GET_USER_BY_ID.callbackRoute())
            } else {
                rabbitMQProducer.sendNullMessage(RabbitMQ.MSCMN_GET_USER_BY_ID.callbackRoute())
            }
        } catch (e: Exception) {
            println("Error processing message: ${e.message}")
        }
    }
}