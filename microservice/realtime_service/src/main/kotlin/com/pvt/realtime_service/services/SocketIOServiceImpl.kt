package com.pvt.realtime_service.services

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.pvt.realtime_service.constants.RabbitMQ
import com.pvt.realtime_service.models.dtos.OnlineStatusDTO
import com.pvt.realtime_service.models.dtos.RabbitMessageDTO
import com.pvt.realtime_service.utils.SocketIOSessionIDStore
import com.pvt.realtime_service.utils.extension.asUUID
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class SocketIOServiceImpl(
    val socketIOServer: SocketIOServer
): SocketIOService {
    @Autowired
    private lateinit var onlineService: OnlineService

    override fun run() {
        connectionListener()
        disconnectionListener()
        socketIOServer.start()
    }

    override fun sendMessage(message: Any, endpoint: String, receiver: UUID) {
        try {
            val sessionID = SocketIOSessionIDStore.findByUserID(receiver)
            if (sessionID != null) {
                socketIOServer.getClient(sessionID).sendEvent(endpoint, message)
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

    private fun extractUserID(client: SocketIOClient): UUID {
        return client.handshakeData.httpHeaders.get("userID").asUUID()
    }
}