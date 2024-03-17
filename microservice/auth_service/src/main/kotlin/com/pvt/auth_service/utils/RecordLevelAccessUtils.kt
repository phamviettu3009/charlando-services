package com.pvt.auth_service.utils

import com.pvt.auth_service.constants.Common

object RecordLevelAccessUtils {
    private val list = Common.LIST_RECORD_LEVEL_ACCESS

    fun shouldValidation(path: String, method: String): Boolean {
        return list.any { isMatchPath(path, it.path) && method == it.method}
    }

    private fun isMatchPath(path: String, pattern: String): Boolean {
        val regex = Regex("^${pattern}\$")
        return regex.matches(path)
    }
}