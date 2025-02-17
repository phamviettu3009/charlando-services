package com.pvt.channel_service.services

import com.pvt.channel_service.constants.RabbitMQ
import com.pvt.channel_service.models.dtos.NotificationMessageDTO
import com.pvt.channel_service.models.dtos.RequestDTO
import com.pvt.channel_service.publisher.RabbitMQProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class CallServiceImpl: CallService {
    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var memberService: MemberService

    override fun makeCall(request: RequestDTO<Unit>) {
        val ownerID = request.jwtBody.userID!!
        val channelID: UUID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val members = memberService.findAllByChannelID(channelID)
        val memberIDs = members.map { it.userID }

        if (!memberIDs.contains(ownerID)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        val caller = userService.getUserByID(ownerID)

        val payload = mapOf(
            "aps" to mapOf(
                "alert" to mapOf(
                    "title" to "Calling",
                    "body" to "${caller.fullName} is calling"
                )
            ),
            "caller" to mapOf(
                "id" to caller.id,
                "name" to caller.fullName
            ),
            "callee" to mapOf(
                "channelID" to channelID
            ),
            "callType" to "video"
        )

        val notificationMessage = NotificationMessageDTO(payload, memberIDs.filter { it != ownerID })
        rabbitMQProducer.sendMessage(notificationMessage, RabbitMQ.MSCMN_WAKE_UP_DEVICES.route())
    }
}