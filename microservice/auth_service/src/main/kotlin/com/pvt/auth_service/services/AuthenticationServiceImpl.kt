package com.pvt.auth_service.services

import com.pvt.auth_service.constants.*
import com.pvt.auth_service.models.dtos.*
import com.pvt.auth_service.models.entitys.*
import com.pvt.auth_service.publisher.RabbitMQProducer
import com.pvt.auth_service.repositories.*
import com.pvt.auth_service.utils.HashString
import com.pvt.auth_service.utils.VerificationCode
import com.pvt.auth_service.utils.date.CompareDateTime
import com.pvt.auth_service.utils.cache.JWTCache
import com.pvt.auth_service.utils.jwt.JwtUtils
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class AuthenticationServiceImpl(
    val rabbitMQProducer: RabbitMQProducer,
    val deviceRepository: DeviceRepository
) : AuthenticationService {
    @Autowired
    private lateinit var authRepository: AuthenticationRepository

    @Autowired
    private lateinit var tenantRepository: TenantRepository

    @Autowired
    private lateinit var emailService: EmailService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var roleUserMappingRepository: RoleUserMappingRepository

    @Autowired
    private lateinit var authorizationService: AuthorizationService

    private fun removeJWTCacheByUserIDAndDeviceIDAndTenantCode(userID: UUID, deviceID: String, tenantCode: String) {
        JWTCache.removeCacheByUserIDAndDeviceIDAndTenantCode(userID = userID, deviceID = deviceID, tenantCode = tenantCode)
    }

    private fun removeJWTCacheByUserID(userID: UUID) {
        JWTCache.removeCacheByUserID(userID)
    }

    @Transactional
    private fun createAuthentication(authentication: AuthenticationEntity): AuthenticationEntity {
        return authRepository.saveAndFlush(authentication)
    }

    @Transactional
    private fun findTenantByTenantCode(tenantCode: String): TenantEntity {
        return tenantRepository.findByTenantCode(tenantCode = tenantCode).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant invalid!")
        }
    }

    @Transactional
    private fun findAccountByUserNameAndTenantCode(userName: String, tenantCode: String): AuthenticationEntity {
        return authRepository.findByUserNameAndTenantCode(userName = userName, tenantCode = tenantCode).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found!")
        }
    }

    @Transactional
    private fun findAccountByUserIDAndTenantCode(userID: UUID, tenantCode: String): AuthenticationEntity {
        return authRepository.findByUserIDAndTenantCode(userID, tenantCode).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found!")
        }
    }

    @Transactional
    private fun setRole(authentication: AuthenticationEntity) {
        val role = roleRepository.findByRoleNameAndTenantCodeAndAuthStatus("DEFAULT", authentication.tenantCode).orElseThrow()
        val authorization = RoleUserMappingEntity(userID = authentication.userID, roleID = role.id)
        roleUserMappingRepository.save(authorization)
    }

    @Transactional
    private fun isAuthStatusActive(authStatus: String?) {
        if (authStatus != AuthStatus.ACTIVE) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "This account is inactive!")
        }
    }

    @Transactional
    private fun isValidVerifyCode(authentication: AuthenticationEntity, verifyCode: String) {
        val verifyCodeHashed = authentication.verifyCode
        val isVerifyCodeValid = HashString.verifyString(verifyCode, verifyCodeHashed.toString(), Common.SALT)
        if (!isVerifyCodeValid) throw ResponseStatusException(
            HttpStatus.EXPECTATION_FAILED,
            "Invalid verification code!"
        )

        val verifyCodeMakerDate = authentication.verifyCodeMakerDate ?: throw ResponseStatusException(
            HttpStatus.EXPECTATION_FAILED,
            "The verification code has expired!"
        )
        val isValidCodeMakerDate = CompareDateTime
            .compareDateTimeWithCurrentDate(date = verifyCodeMakerDate)
            .isDiscrepancy(10)
        if (!isValidCodeMakerDate) throw ResponseStatusException(
            HttpStatus.EXPECTATION_FAILED,
            "The verification code has expired!"
        )
    }

    @Transactional
    private fun createUserByTenant(tenantCode: String, userID: UUID) {
        when(tenantCode) {
            Tenant.Code.MSC -> {
                userService.putUserMessageToRabbit(
                    userID ,
                    RabbitMQ.MSC_CREATE_RECORD_USER.route(),
                    RabbitMQ.MSC_CREATE_RECORD_USER.callbackQueue()
                )
            }
        }
    }

    @Transactional
    private fun makeToken(
        user: String,
        userID: UUID,
        tenantCode: String,
        deviceID: String,
        authenticationID: UUID,
        deviceSystemName: String,
        deviceName: String,
        os: String,
        description: String
    ): AuthenticationSuccessResponseDTO {
        val now = Date()
        // 3600000 = 1 hour
        val expirationAccessToken = Date(now.time + 3600000 * 24 * 1)
        val expirationRefreshToken = Date(now.time + 3600000 * 24 * 15)

        val jwtBody = JWTBodyDTO(
            user = user,
            userID = userID,
            type = "",
            authID = authenticationID,
            deviceID = deviceID,
            tenantCode = tenantCode
        )

        val accessToken: String = JwtUtils
            .createJWT(
                jwtBody = jwtBody.copy(type = "access"),
                issuedAt = now,
                expiration = expirationAccessToken
            )
        val refreshToken: String = JwtUtils
            .createJWT(
                jwtBody = jwtBody.copy(type = "refresh"),
                issuedAt = now,
                expiration = expirationRefreshToken
            )

        val device = deviceRepository.findByDeviceIDAndAuthenticationID(deviceID, authenticationID).or {
            Optional.ofNullable(
                DeviceEntity(
                    deviceID = deviceID,
                    userID = userID,
                    authenticationID = authenticationID,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    deviceName = deviceName,
                    deviceSystemName = deviceSystemName,
                    os = os,
                    description = description
                )
            )
        }

        val deviceUpdate = device.get()

        if (deviceUpdate.authStatus != AuthStatus.ACTIVE) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "This device is inactive!")
        }

        deviceUpdate.accessToken = accessToken
        deviceUpdate.refreshToken = refreshToken
        deviceUpdate.mostRecentLoginTime = Date()
        deviceRepository.saveAndFlush(deviceUpdate)

        return AuthenticationSuccessResponseDTO(accessToken, refreshToken)
    }

    @Transactional
    override fun registerWithEmail(account: AccountRegisterDTO): RegisterResponseDTO {
        val userName = account.user
        val password = account.password
        val tenantCode = account.tenantCode

        val tenant = findTenantByTenantCode(tenantCode)

        val account =
            authRepository.findByUserNameAndTenantCode(userName = userName, tenantCode = tenant.tenantCode).orElse(null)
        if (account == null) {
            val salt: String = Common.SALT + userName
            val hashedPassword: String = HashString.hashString(password, salt)
            val verifyCode = VerificationCode.generateVerificationCode()

            val avatar = "b1b696bb-ddb9-4ad9-89c3-f39f81f14157" // empty_avatar (common)
            val user = UserEntity(id = UUID.randomUUID(), fullName = userName, avatar = avatar)
            val userCreated = userService.createUser(user)

            val recordLevelAccessPayload = RecordLevelAccessPayloadDTO(
                accessContent = "/resource/get/$avatar",
                method = "GET",
                recordStatus = "public",
                ownerID = userCreated.id,
                userAccessIDs = listOf()
            )
            authorizationService.createRecordLevelAccess(recordLevelAccessPayload)

            val authentication = AuthenticationEntity(
                userName = userName,
                hashPassword = hashedPassword,
                recordStatus = AuthenticationRecordStatus.UNVERIFIED,
                verifyCode = verifyCode.getHashCode(),
                verifyCodeMakerDate = Date(),
                tenantCode = tenant.tenantCode,
                userID = userCreated.id
            )
            createAuthentication(authentication)

            emailService.sendVerifyCode(verifyCode = verifyCode.getCode(), sendTo = userName)
            return RegisterResponseDTO(message = "Successfully", status = AuthenticationRecordStatus.UNVERIFIED)
        } else {
            return when (account.recordStatus) {
                AuthenticationRecordStatus.VERIFIED -> throw ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This account already exists!"
                )
                AuthenticationRecordStatus.UNVERIFIED -> RegisterResponseDTO(
                    message = "This account has not been verified!",
                    status = AuthenticationRecordStatus.UNVERIFIED
                )
                else -> throw ResponseStatusException(HttpStatus.NOT_FOUND, "This account not found!")
            }
        }
    }

    @Transactional
    override fun verifyAccount(accountVerify: AccountVerifyDTO): RegisterResponseDTO {
        val userName = accountVerify.user
        val verifyCode = accountVerify.verifyCode
        val tenantCode = accountVerify.tenantCode

        val tenant = findTenantByTenantCode(tenantCode)
        val account = findAccountByUserNameAndTenantCode(userName, tenant.tenantCode)

        if (account.recordStatus == AuthenticationRecordStatus.UNVERIFIED) {
            isValidVerifyCode(account, verifyCode)
            account.recordStatus = AuthenticationRecordStatus.VERIFIED
            val savedAccount = authRepository.saveAndFlush(account)
            if (savedAccount.recordStatus == AuthenticationRecordStatus.VERIFIED) {
                setRole(account)
                createUserByTenant(tenantCode, userID = account.userID)
                return RegisterResponseDTO(message = "Successfully", status = AuthenticationRecordStatus.VERIFIED)
            } else {
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update record status!")
            }
        } else if (account.recordStatus == AuthenticationRecordStatus.VERIFIED) {
            throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "This account is verified!")
        }
        throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Account invalid!")
    }

    @Transactional
    override fun loginWithEmail(accountPassword: AccountPasswordDTO): AuthenticationSuccessResponseDTO {
        val tenantCode = accountPassword.tenantCode
        val userName = accountPassword.user
        val password = accountPassword.password
        val deviceID = accountPassword.deviceID
        val deviceSystemName = accountPassword.deviceSystemName
        val deviceName = accountPassword.deviceName
        val os = accountPassword.os
        val description = accountPassword.description
        val salt: String = Common.SALT + userName
        val authInfo: AuthenticationEntity =
            authRepository.findByUserNameAndTenantCode(userName = userName, tenantCode = tenantCode)
                .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password!") }

        isAuthStatusActive(authInfo.authStatus)

        if (authInfo.recordStatus != AuthenticationRecordStatus.VERIFIED) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "This account is not verified!")
        }

        val userID = authInfo.userID
        val authenticationID = authInfo.id

        val isVerify = HashString.verifyString(password, authInfo.hashPassword.toString(), salt)
        if (isVerify) {
            JWTCache.removeCacheByUserIDAndDeviceIDAndTenantCode(userID, deviceID, tenantCode)
            return makeToken(userName, userID, tenantCode, deviceID, authenticationID, deviceSystemName, deviceName, os, description)
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password!")
        }
    }

    override fun getNewAccessToken(refreshToken: String): String {
        val validate = JwtUtils.validateToken(token = refreshToken)

        if (validate is Boolean) {
            val jwtBody = JwtUtils.getBodyFromToken(refreshToken)
            if (jwtBody.type == "refresh") {
                val now = Date()
                val expirationAccessToken = Date(now.time + 3600000 * 24 * 1)
                val userID = jwtBody.userID!!
                val tenantCode = jwtBody.tenantCode!!

                val account = findAccountByUserIDAndTenantCode(userID, tenantCode)
                isAuthStatusActive(account.authStatus)

                val device = deviceRepository.findByRefreshToken(refreshToken).orElseThrow {
                    throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalid!")
                }
                isAuthStatusActive(device.authStatus)

                val jwtBody = JWTBodyDTO(
                    user = account.userName,
                    userID = account.userID,
                    type = "access",
                    authID = account.id,
                    deviceID = device.deviceID,
                    tenantCode = account.tenantCode
                )

                val accessToken: String = JwtUtils
                    .createJWT(
                        jwtBody = jwtBody,
                        issuedAt = now,
                        expiration = expirationAccessToken
                    )

                device.accessToken = accessToken
                val deviceUpdated = deviceRepository.saveAndFlush(device)

                if (deviceUpdated.accessToken == accessToken) {
                    return accessToken
                }
            }
        }

        if (validate is JwtFilterExceptionDTO) {
            throw ResponseStatusException(validate.status, validate.message)
        }

        throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT invalid!")
    }

    override fun resendVerifyCode(account: AccountDTO): String {
        val userName = account.user
        val tenantCode = account.tenantCode
        val account = findAccountByUserNameAndTenantCode(userName, tenantCode)
        isAuthStatusActive(account.authStatus)

        if (account.recordStatus == AuthenticationRecordStatus.UNVERIFIED) {
            val verifyCode = VerificationCode.generateVerificationCode()
            account.verifyCode = verifyCode.getHashCode()
            account.verifyCodeMakerDate = Date()
            authRepository.saveAndFlush(account)
            emailService.sendVerifyCode(verifyCode = verifyCode.getCode(), sendTo = userName)
            return "Resend verify code successfully"
        } else if (account.recordStatus == AuthenticationRecordStatus.VERIFIED) {
            throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "This account is verified!")
        }

        throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Resend verify code error!")
    }

    @Transactional
    override fun changePassword(userID: UUID, account: AccountChangePasswordDTO): String {
        val tenantCode = account.tenantCode
        val oldPassword = account.oldPassword
        val newPassword = account.newPassword

        val account = findAccountByUserIDAndTenantCode(userID, tenantCode)
        isAuthStatusActive(account.authStatus)

        val salt: String = Common.SALT + account.userName
        val isVerify = HashString.verifyString(oldPassword, account.hashPassword.toString(), salt)

        if (isVerify) {
            val hashedPassword: String = HashString.hashString(newPassword, salt)
            account.hashPassword = hashedPassword
            authRepository.saveAndFlush(account)
            return "Password changed successfully"
        } else {
            throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Incorrect old password!")
        }
    }

    @Transactional
    override fun forgotPassword(account: AccountForgotPasswordDTO): String {
        val userName = account.user
        val tenantCode = account.tenantCode
        val newPassword = account.newPassword
        val verifyCode = account.verifyCode

        val account = findAccountByUserNameAndTenantCode(userName, tenantCode)
        isAuthStatusActive(account.authStatus)
        isValidVerifyCode(account, verifyCode)

        val salt: String = Common.SALT + userName
        val hashedPassword: String = HashString.hashString(newPassword, salt)
        account.hashPassword = hashedPassword
        authRepository.saveAndFlush(account)
        return "Password changed successfully"
    }

    @Transactional
    override fun requestForgotPassword(account: AccountDTO): String {
        val userName = account.user
        val tenantCode = account.tenantCode

        val account = findAccountByUserNameAndTenantCode(userName, tenantCode)
        isAuthStatusActive(account.authStatus)
        val verifyCode = VerificationCode.generateVerificationCode()
        account.verifyCode = verifyCode.getHashCode()
        account.verifyCodeMakerDate = Date()
        authRepository.saveAndFlush(account)

        emailService.sendVerifyCode(verifyCode = verifyCode.getCode(), sendTo = userName)
        return "Successfully"
    }

    @Transactional
    override fun logoutAllDevice(account: AccountDTO): String {
        val userName = account.user
        val tenantCode = account.tenantCode

        val account = findAccountByUserNameAndTenantCode(userName, tenantCode)
        isAuthStatusActive(account.authStatus)

        val devices = deviceRepository.findAllByAndAuthenticationID(authenticationID = account.id)
        if (devices.isEmpty()) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found!")

        for (device in devices) {
            device.refreshToken = null
            device.accessToken = null
            device.mostRecentLogoutTime = Date()
            deviceRepository.save(device)
        }

        removeJWTCacheByUserID(account.userID)

        return "Logged out of all accounts successfully"
    }

    @Transactional
    override fun logoutDeviceByID(account: AccountDeviceDTO): String {
        val userName = account.user
        val tenantCode = account.tenantCode
        val deviceID = account.deviceID

        val account = findAccountByUserNameAndTenantCode(userName, tenantCode)
        isAuthStatusActive(account.authStatus)

        val device = deviceRepository.findByDeviceIDAndUserID(deviceID, account.userID).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found!")
        }

        device.refreshToken = null
        device.accessToken = null
        device.mostRecentLogoutTime = Date()
        deviceRepository.save(device)

        removeJWTCacheByUserIDAndDeviceIDAndTenantCode(device.userID!! ,deviceID, tenantCode)

        return "Logged out of the account successfully"
    }

    @Transactional
    override fun authentication(jwtToken: String?, path: String?, method: String): JWTBodyDTO {
        val jwtBody = validationJWT(jwtToken)
        val jwtUser = jwtBody.asJWTUser(jwtToken!!)
        validationUserContentAccessPermission(jwtUser.userID, path, method)
        return jwtBody
    }

    override fun getDevices(userID: UUID): List<DeviceDTO> {
        val devices = deviceRepository.findAllByUserID(userID)
        return devices.map { it.asDeviceDTO() }
    }

    private fun validationJWT(jwtToken: String?): JWTBodyDTO {
        val result = JwtUtils.validateToken(jwtToken)

        if (result is JwtFilterExceptionDTO) {
            throw ResponseStatusException(result.status, result.message)
        }

        val jwtBody = JwtUtils.getBodyFromToken(jwtToken!!)
        val jwtUser = jwtBody.asJWTUser(jwtToken)

        val hasCache = JWTCache.hasCache(jwtUser)
        if (!hasCache) {
            deviceRepository.findByAccessToken(jwtToken).orElseThrow {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT invalid!")
            }
            JWTCache.setCache(jwtUser)
        }
        return jwtBody
    }

    private fun validationUserContentAccessPermission(userID: UUID, path: String?, method: String) {
        if (path == null || path.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }

        authorizationService.validationDomainLevelAccess(userID, path, method)
        authorizationService.validationRecordLevelAccess(userID, path, method)
    }

    @RabbitListener(queues = [RabbitMQ.Listener.MSCMN_VALIDATION_JWT])
    private fun validationJWT(data: RabbitMessageDTO<String>) {
        try {
            val token = data.message
            val jwtBody = validationJWT(token)
            rabbitMQProducer.sendMessage(jwtBody.userID.toString(), RabbitMQ.MSCMN_VALIDATION_JWT.callbackRoute())
        } catch (e: Exception) {
            println(e)
            rabbitMQProducer.sendNullMessage(RabbitMQ.MSCMN_VALIDATION_JWT.callbackRoute())
        }
    }
}