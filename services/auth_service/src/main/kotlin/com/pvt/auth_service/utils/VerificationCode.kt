package com.pvt.auth_service.utils

import com.pvt.auth_service.constants.Common
import java.util.*

object VerificationCode {
    fun generateVerificationCode(length: Int = 6): VerificationCodeBuilder {
        val random = Random(System.currentTimeMillis())
        val code = StringBuilder()
        repeat(length) {
            val digit = random.nextInt(10)
            code.append(digit)
        }

        return VerificationCodeBuilder(code.toString())
    }
}

class VerificationCodeBuilder(private val code: String) {
    fun getHashCode(): String {
        return HashString.hashString(code, Common.SALT)
    }

    fun getCode(): String {
        return code
    }
}