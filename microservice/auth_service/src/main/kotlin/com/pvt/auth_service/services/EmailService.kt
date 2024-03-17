package com.pvt.auth_service.services

interface EmailService {
    fun sendVerifyCode(verifyCode: String, sendTo: String)
}