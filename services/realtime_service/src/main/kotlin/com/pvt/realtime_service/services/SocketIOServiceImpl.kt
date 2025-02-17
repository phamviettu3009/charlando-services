package com.pvt.realtime_service.services

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.google.gson.Gson
import com.pvt.realtime_service.models.dtos.*
import com.pvt.realtime_service.utils.SocketIOSessionIDStore
import com.pvt.realtime_service.utils.extension.asUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SocketIOServiceImpl(
    val socketIOServer: SocketIOServer
): SocketIOService {
    val gson: Gson = Gson()

    @Autowired
    private lateinit var onlineService: OnlineService

    override fun run() {
        connectionListener()
        disconnectionListener()
        typingListener()
        socketIOServer.start()
    }

    override fun sendMessage(message: Any, endpoint: String, receiver: UUID) {
        try {
            val sessionID = SocketIOSessionIDStore.findByUserID(receiver)
            if (sessionID != null) {
                val json = gson.toJson(message)
                socketIOServer.getClient(sessionID).sendEvent(endpoint, json)
            }
        } catch (e: Exception) {
            println("Error processing message: ${e.message}")
        }
    }

    private fun connectionListener() {
        socketIOServer.addConnectListener { client ->
            val userID = extractUserID(client)
            val sessionID = client.sessionId
            println("socket.io connection: $userID : $sessionID")
            SocketIOSessionIDStore.setSession(userID, sessionID)
            onlineService.onlineStatus(OnlineStatusDTO(userID, true))
        }
    }

    private fun disconnectionListener() {
        socketIOServer.addDisconnectListener { client ->
            val userID = extractUserID(client)
            println("socket.io disconnection: $userID")
            onlineService.onlineStatus(OnlineStatusDTO(userID, false))
            SocketIOSessionIDStore.removeSession(userID)
        }
    }

    private fun typingListener() {
        socketIOServer.addEventListener("typing", String::class.java) { client, data, _ ->
            val userID = extractUserID(client)
            val typing: TypingDTO = gson.fromJson(data, TypingDTO::class.java)
            val channelID = typing.channelID

            val users = onlineService.typing(typing).filter { it.id != userID }
            for (user in users) {
                val sessionID: UUID = SocketIOSessionIDStore.findByUserID(user.id) ?: continue
                val responseJson = gson.toJson(typing.asTypingResponseDTO(user))
                socketIOServer.getClient(sessionID).sendEvent("typing/channel/$channelID", responseJson)
            }
        }
    }

    private fun extractUserID(client: SocketIOClient): UUID {
        return client.handshakeData.httpHeaders.get("userID").asUUID()
    }
}