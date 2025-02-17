package com.pvt.realtime_service.services

import com.eatthepath.pushy.apns.ApnsClient
import com.eatthepath.pushy.apns.ApnsClientBuilder
import com.eatthepath.pushy.apns.DeliveryPriority
import com.eatthepath.pushy.apns.PushType
import com.eatthepath.pushy.apns.auth.ApnsSigningKey
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification
import com.eatthepath.pushy.apns.util.TokenUtil
import com.google.gson.Gson
import com.pvt.realtime_service.constants.RabbitMQ
import com.pvt.realtime_service.publisher.RabbitMQProducer
import com.pvt.realtime_service.publisher.receiveDeviceTokenDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.ExecutionException


@Service
class CallServiceImpl: CallService {
    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    val gson = Gson()
    private val topic: String = "com.tupham.charlando.voip"
    private val useSandbox: Boolean = true

    private val credential = ClassPathResource("voip.p12")
    private val key = ClassPathResource("auth_key.p8")

    private val apnsClient: ApnsClient = ApnsClientBuilder()
        .setApnsServer(
            if (useSandbox) ApnsClientBuilder.DEVELOPMENT_APNS_HOST else ApnsClientBuilder.PRODUCTION_APNS_HOST
        )
        .setSigningKey(ApnsSigningKey.loadFromPkcs8File(getAuthFile(),"KUWN39P6HA","PKHYGGWWM2"))
        .build()

    override fun wakeUpDevices(message: Map<String, Any>, receivers: List<UUID>) {
        rabbitMQProducer.sendMessage(receivers, RabbitMQ.MSCMN_GET_DEVICE.route())
        val responseDeviceTokens = rabbitMQProducer.receiveDeviceTokenDTO(RabbitMQ.MSCMN_GET_DEVICE.callbackQueue())

        val deviceTokens = responseDeviceTokens.message ?: listOf()

        for (deviceToken in deviceTokens) {
            val pushkitToken = deviceToken.pushkitToken ?: continue
            val payload = gson.toJson(message)
            wakeUpDevice(pushkitToken, payload)
        }
    }

    private fun wakeUpDevice(deviceToken: String, payload: String) {
        val sanitizedToken = TokenUtil.sanitizeTokenString(deviceToken)
        val pushNotification = SimpleApnsPushNotification(sanitizedToken, topic, payload, null, DeliveryPriority.IMMEDIATE, PushType.VOIP)

        try {
            val pushNotificationResponse = apnsClient.sendNotification(pushNotification).get()
            if (pushNotificationResponse.isAccepted) {
                "Push notification accepted by APNs gateway."
            } else {
                "Notification rejected by the APNs gateway: ${pushNotificationResponse.rejectionReason}"
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            "Failed to send push notification :: ${e.message}"
        } catch (e: ExecutionException) {
            println("error: $e")
            "Failed to send push notification: ${e.cause?.message}"
        }
    }

    private fun inputStreamToFile(inputStream: InputStream, file: File) {
        try {
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getAuthFile(): File {
        val outputFile = File("output_auth_key.p8")
        val keyResource = ClassPathResource("auth_key.p8")
        keyResource.inputStream.use { inputStream ->
            inputStreamToFile(inputStream, outputFile)
        }
        return outputFile
    }
}