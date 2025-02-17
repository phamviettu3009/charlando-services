package com.pvt.auth_service.utils.date

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object CompareDateTime {
    fun compareDateTimeWithCurrentDate(date: Date): CompareDateTimeWithCurrentDateBuilder {
        val currentDateTime = LocalDateTime.ofInstant(Date().toInstant(), ZoneId.systemDefault())
        val dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())

        return CompareDateTimeWithCurrentDateBuilder(currentDateTime, dateTime)
    }
}

class CompareDateTimeWithCurrentDateBuilder(currentDateTime: LocalDateTime, dateTime: LocalDateTime) {
    private val duration = Duration.between(dateTime, currentDateTime)

    fun isEqual(): Boolean {
        val size: Int = 0
        val minutesDifference = duration.toMinutes()
        return minutesDifference == size.toLong()
    }

    fun isDiscrepancy(size: Long): Boolean {
        val minutesDifference = duration.toMinutes()
        return minutesDifference <= size
    }
}