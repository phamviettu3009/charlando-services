package com.pvt.channel_service.utils.converter

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

object DateTimeConverter {
    fun convertDisplay(dateTime: Date?): String {
        if (dateTime == null || dateTime.toString().isEmpty()) {
            return ""
        }

        val formatterHHmm = DateTimeFormatter.ofPattern("HH:mm")
        val formatterFull = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val localDateTime = LocalDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault())

        val now = LocalDateTime.now()
        val duration = Duration.between(localDateTime, now)
        val hoursDiff = duration.toHours()

        return when {
            localDateTime.toLocalDate() == now.toLocalDate() -> {
                if (hoursDiff < 1) {
                    val minutesDiff = duration.toMinutes()
                    when {
                        minutesDiff < 1 -> "Just now"
                        minutesDiff == 1L -> "1 minute ago"
                        else -> "$minutesDiff minutes ago"
                    }
                } else {
                    localDateTime.format(formatterHHmm)
                }
            }
            else -> localDateTime.format(formatterFull)
        }
    }

    fun convertConsecutiveMessage(dateTime: Date, preDateTime: Date): Boolean {
        val currentDateTime = LocalDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault())
        val previousDateTime = LocalDateTime.ofInstant(preDateTime.toInstant(), ZoneId.systemDefault())

        val duration = Duration.between(previousDateTime, currentDateTime)
        val minutesDifference = duration.toMinutes()
        if (minutesDifference <= 5) {
            return true
        }

        return false
    }

    fun convertIsoStringToDate(isoString: String): Date {
        val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        return try {
            val zonedDateTime = ZonedDateTime.parse(isoString, dateTimeFormatter)
            Date.from(zonedDateTime.toInstant())
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format: $isoString")
        }
    }
}