package com.pvt.auth_service.utils.cache

import java.util.HashMap
import java.util.UUID

object RecordLevelAccessCache {
    private val recordLevelAccessCache: HashMap<String, MutableMap<String, Boolean>> = hashMapOf()

    fun hasCache(userID: UUID, path: String, method: String): Boolean {
        val userIDKey = userID.toString()
        return recordLevelAccessCache[userIDKey]?.get(path + method) == true
    }

    fun setCache(userID: UUID, path: String, method: String) {
        val userIDKey = userID.toString()

        if (recordLevelAccessCache[userIDKey] != null) {
            recordLevelAccessCache[userIDKey]?.put(path + method, true)
        } else {
            recordLevelAccessCache[userIDKey] = mutableMapOf(path + method to true)
        }
    }
}