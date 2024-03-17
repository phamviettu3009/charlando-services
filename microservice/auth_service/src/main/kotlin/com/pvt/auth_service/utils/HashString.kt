package com.pvt.auth_service.utils

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object HashString {
    private const val SALT = "pvt@001#"
    private val passwordEncoder = BCryptPasswordEncoder()

    private fun sha256(password: String, salt: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(salt.toByteArray())
        val hash = messageDigest.digest(password.toByteArray(StandardCharsets.UTF_8))
        return String.format("%064x", BigInteger(1, hash))
    }

    fun verifyString(value: String, hashedValue: String, salt: String): Boolean {
        val hashedStep1 = sha256(value, salt)
        return passwordEncoder.matches(hashedStep1 + SALT, hashedValue)
    }

    fun hashString(value: String, salt: String): String {
        val hashedStep1 = sha256(value, salt)
        return passwordEncoder.encode(hashedStep1 + SALT)
    }
}