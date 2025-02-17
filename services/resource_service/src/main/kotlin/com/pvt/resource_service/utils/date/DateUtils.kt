package com.pvt.resource_service.utils.date

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateUtils {
    fun getCurrentTimestamp(): String {
        val timestamp = Instant.now().toEpochMilli()
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS"))
    }
}