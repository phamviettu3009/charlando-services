package com.pvt.realtime_service.services

import com.google.firebase.messaging.*
import com.pvt.realtime_service.constants.RabbitMQ
import com.pvt.realtime_service.publisher.RabbitMQProducer
import com.pvt.realtime_service.publisher.receiveDeviceTokenDTO
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
    override fun sendNotification(message: Map<String, Any>, receivers: List<UUID>) {
        try {
            rabbitMQProducer.sendMessage(receivers, RabbitMQ.MSCMN_GET_DEVICE.route())
            val responseDeviceFirebaseTokens = rabbitMQProducer.receiveDeviceTokenDTO(RabbitMQ.MSCMN_GET_DEVICE.callbackQueue())

            val deviceFirebaseTokens = responseDeviceFirebaseTokens.message ?: listOf()
            val notification: Notification = Notification
                .builder()
                .build()

            for (deviceFirebaseToken in deviceFirebaseTokens) {
                val token = deviceFirebaseToken.fcmtoken ?: continue
                sendNotification(notification, token, message)
            }
        } catch (e: Exception) {
            println("send notification error: $e")
        }
    }

    private fun sendNotification(notification: Notification, token: String,  data: Map<String, Any>) {
         try {
             var apnsHeaders = mutableMapOf("apns-priority" to "10")

             if (data["messageID"] != null) {
                 apnsHeaders["apns-collapse-id"] = data["messageID"] as String
             }

             if (data["deleteNotifyID"] != null) {
                 val deleteNotifyID = data["deleteNotifyID"] as String? ?: return
                 deleteNotification(notification, token, deleteNotifyID)
                 return
             }

             val title = data["title"] as String? ?: return
             val body = data["body"] as String? ?: return
             val dataPut = data as Map<String, String>? ?: return

             val aps = Aps.builder()
                 .setBadge(1)
                 .setSound("default")
                 .setAlert(ApsAlert.builder().setTitle(title).setBody(body).build())
                 .build()
             val apnsConfig = ApnsConfig.builder().putAllHeaders(apnsHeaders).setAps(aps).build()

             val androidConfig = AndroidConfig
                 .builder()
                 .setPriority(AndroidConfig.Priority.HIGH)
                 .build()

             val message: Message = Message
                 .builder()
                 .setToken(token)
                 .setNotification(notification)
                 .putAllData(dataPut)
                 .setApnsConfig(apnsConfig)
                 .setAndroidConfig(androidConfig)
                 .build()
             firebaseMessaging.send(message)
         } catch (e: Exception) {
             println("notification error: $e")
         }
    }

    private fun deleteNotification(notification: Notification, token: String, deleteNotifyID: String) {
        var apnsHeaders = mutableMapOf("apns-priority" to "5")
        val aps = Aps.builder()
            .setBadge(0)
            .setSound("default")
            .setContentAvailable(true)
            .build()
        val apnsConfig = ApnsConfig.builder().putAllHeaders(apnsHeaders).setAps(aps).build()

        val androidConfig = AndroidConfig
            .builder()
            .setPriority(AndroidConfig.Priority.HIGH)
            .build()

        val message: Message = Message
            .builder()
            .setToken(token)
            .setNotification(notification)
            .putAllData(mapOf("deleteNotifyID" to deleteNotifyID))
            .setApnsConfig(apnsConfig)
            .setAndroidConfig(androidConfig)
            .build()
        firebaseMessaging.send(message)
    }
}