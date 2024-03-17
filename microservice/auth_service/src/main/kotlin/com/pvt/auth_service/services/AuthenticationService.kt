package com.pvt.auth_service.services

import com.pvt.auth_service.models.dtos.*

interface AuthenticationService {
    fun registerWithEmail(account: AccountRegisterDTO): RegisterResponseDTO
    fun verifyAccount(accountVerify: AccountVerifyDTO): RegisterResponseDTO
    fun loginWithEmail(accountPassword: AccountPasswordDTO): AuthenticationSuccessResponseDTO
    fun getNewAccessToken(refreshToken: JwtDTO): JwtDTO
    fun resendVerifyCode(account: AccountDTO): String
    fun changePassword(account: AccountChangePasswordDTO): String
    fun forgotPassword(account: AccountForgotPasswordDTO): String
    fun requestForgotPassword(account: AccountDTO): String
    fun logoutAllDevice(account: AccountDTO): String
    fun logoutDeviceByID(account: AccountDeviceDTO): String
    fun authentication(jwtToken: String?, path: String?, method: String): JWTBodyDTO
}