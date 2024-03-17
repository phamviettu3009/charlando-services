package com.pvt.realtime_service.services

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.pvt.realtime_service.utils.SocketIOSessionIDStore
import com.pvt.realtime_service.utils.extension.asUUID
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SocketIOServiceImpl(
    val socketIOServer: SocketIOServer
): SocketIOService {
    override fun run() {
        connectionListener()
        disconnectionListener()
        socketIOServer.start()
    }

    override fun sendMessage(message: Any, endpoint: String, receiver: UUID) {
        socketIOServer.getClient(receiver).sendEvent(endpoint, message)
    }

    private fun connectionListener() {
        socketIOServer.addConnectListener { client ->
            val userID = extractUserID(client)
            SocketIOSessionIDStore.setSession(userID, client.sessionId)
        }
    }

    private fun disconnectionListener() {
        socketIOServer.addDisconnectListener { client ->
            val userID = extractUserID(client)
            SocketIOSessionIDStore.removeSession(userID)
        }
    }

    private fun extractUserID(client: SocketIOClient): UUID {
        return client.handshakeData.httpHeaders.get("userID").asUUID()
    }
}