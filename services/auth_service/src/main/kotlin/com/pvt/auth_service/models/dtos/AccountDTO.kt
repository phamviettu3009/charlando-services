package com.pvt.auth_service.models.dtos

import java.util.UUID

data class AccountRegisterDTO(
    val user: String,
    val password: String,
    val tenantCode: String
)

data class AccountPasswordDTO(
    val user: String,
    val password: String,
    val tenantCode: String,
    val deviceID: String,
    val deviceSystemName: String,
    val deviceName: String,
    val os: String,
    val description: String
)

data class AccountDTO(
    val user: String,
    val tenantCode: String
)

data class AccountVerifyDTO(
    val user: String,
    val verifyCode: String,
    val tenantCode: String
)

data class AccountChangePasswordDTO(
    val tenantCode: String,
    val oldPassword: String,
    val newPassword: String
)

data class AccountForgotPasswordDTO(
    val user: String,
    val tenantCode: String,
    val newPassword: String,
    val verifyCode: String
)

data class AccountDeviceDTO(
    val user: String,
    val tenantCode: String,
    val deviceID: String
)
