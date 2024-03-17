package com.pvt.realtime_service.utils

import java.util.UUID

object SocketIOSessionIDStore {
    private val sessions: MutableMap<UUID, UUID> = mutableMapOf()

    fun setSession(userID: UUID, sessionID: UUID) {
        sessions[userID] = sessionID
    }

    fun removeSession(userID: UUID) {
        sessions.remove(userID)
    }

    fun findByUserID(userID: UUID): UUID? {
        return sessions[userID]
    }
}