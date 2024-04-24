package com.pvt.realtime_service.services

import com.google.firebase.messaging.*
import com.pvt.realtime_service.constants.RabbitMQ
import com.pvt.realtime_service.models.dtos.DeviceFirebaseTokenDTO
import com.pvt.realtime_service.publisher.RabbitMQProducer
import com.pvt.realtime_service.publisher.receiveDeviceFirebaseTokenDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class FirebaseMessagingServiceImpl: FirebaseMessagingService {
    @Autowired
    private lateinit var firebaseMessaging: FirebaseMessaging

    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    @Throws(FirebaseMessagingException::class)
    override fun sendNotification(message: Map<String, String>, receivers: List<UUID>) {
        try {
            rabbitMQProducer.sendMessage(receivers, RabbitMQ.MSCMN_GET_DEVICE.route())
            val responseDeviceFirebaseTokens = rabbitMQProducer.receiveDeviceFirebaseTokenDTO(RabbitMQ.MSCMN_GET_DEVICE.callbackQueue())

            val deviceFirebaseTokens = responseDeviceFirebaseTokens.message ?: listOf()
            val notification: Notification = Notification
                .builder()
                .build()

            for (deviceFirebaseToken in deviceFirebaseTokens) {
                val token = deviceFirebaseToken.fcmtoken ?: continue
                println("=======> $token")
                sendNotification(notification, token, message)
            }
        } catch (e: Exception) {
            println("send notification error: $e")
        }
    }

    private fun sendNotification(notification: Notification, token: String,  data: Map<String, String>) {
         try {
             val aps = Aps.builder()
                 .setBadge(1)
                 .setSound("default")
                 .setAlert(ApsAlert.builder().setTitle(data["title"]).setBody(data["body"]).build())
                 .build()
             val apnsConfig = ApnsConfig.builder().putHeader("apns-priority", "10").setAps(aps).build()

             val androidConfig = AndroidConfig
                 .builder()
                 .setPriority(AndroidConfig.Priority.HIGH)
                 .build()

             val message: Message = Message
                 .builder()
                 .setToken(token)
                 .setNotification(notification)
                 .putAllData(data)
                 .setApnsConfig(apnsConfig)
                 .setAndroidConfig(androidConfig)
                 .build()
             firebaseMessaging.send(message)
         } catch (e: Exception) {
             println("notification error: $e")
         }
    }
}