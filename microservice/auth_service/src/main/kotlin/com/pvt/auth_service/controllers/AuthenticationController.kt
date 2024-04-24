package com.pvt.auth_service.controllers

import com.pvt.auth_service.models.dtos.*
import com.pvt.auth_service.services.AuthenticationService
import com.pvt.auth_service.services.AuthorizationService
import com.pvt.auth_service.services.DeviceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController {
    @Autowired
    private lateinit var authenticationService: AuthenticationService

    @Autowired
    private lateinit var deviceService: DeviceService

    @PostMapping("/register-with-email")
    fun registerWithEmail(@RequestBody account: AccountRegisterDTO): ResponseEntity<RegisterResponseDTO> {
        return ResponseEntity(authenticationService.registerWithEmail(account), HttpStatus.OK)
    }

    @PostMapping("/verify-account")
    fun verifyAccount(@RequestBody account: AccountVerifyDTO): ResponseEntity<RegisterResponseDTO> {
        return ResponseEntity(authenticationService.verifyAccount(account), HttpStatus.OK)
    }

    @PostMapping("/login-with-email")
    fun loginWithEmail(
        @RequestBody account: AccountPasswordDTO
    ): ResponseEntity<AuthenticationSuccessResponseDTO> {
        return ResponseEntity(authenticationService.loginWithEmail(account), HttpStatus.OK)
    }

    @PostMapping("/get-new-access-token")
    fun getNewAccessToken(@RequestBody refreshToken: String): ResponseEntity<String> {
        return ResponseEntity(authenticationService.getNewAccessToken(refreshToken), HttpStatus.OK)
    }

    @PostMapping("/resend-verify-code")
    fun resendVerifyCode(@RequestBody account: AccountDTO): ResponseEntity<String> {
        return ResponseEntity(authenticationService.resendVerifyCode(account), HttpStatus.OK)
    }

    @PostMapping("/logout-device")
    fun logoutDevice(
        request: HttpServletRequest,
        @RequestBody deviceID: String
    ): ResponseEntity<String> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO

        val account = AccountDeviceDTO(
            user = jwtBody.user!!,
            deviceID = deviceID,
            tenantCode = jwtBody.tenantCode!!
        )
        return ResponseEntity(authenticationService.logoutDeviceByID(account), HttpStatus.OK)
    }

    @PostMapping("/logout-all-device")
    fun logoutAllDevice(
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO

        val account = AccountDTO(
            user = jwtBody.user!!,
            tenantCode = jwtBody.tenantCode!!
        )
        return ResponseEntity(authenticationService.logoutAllDevice(account), HttpStatus.OK)
    }

    @PostMapping("/validation-jwt")
    fun validationJWT(@RequestBody jwt: JwtValidationDTO): ResponseEntity<JWTBodyDTO> {
        val token = jwt.token
        val path = jwt.path
        val method = jwt.method
        return ResponseEntity(authenticationService.authentication(token, path, method), HttpStatus.OK)
    }

    @GetMapping("/devices")
    fun getDevices(request: HttpServletRequest): ResponseEntity<List<DeviceDTO>> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val userID = jwtBody.userID ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return ResponseEntity(authenticationService.getDevices(userID), HttpStatus.OK)
    }

    @PostMapping("/change-password")
    fun changePassword(request: HttpServletRequest, @RequestBody account: AccountChangePasswordDTO): ResponseEntity<String> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val userID = jwtBody.userID ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return ResponseEntity(authenticationService.changePassword(userID, account), HttpStatus.OK)
    }

    @PostMapping("/request-forgot-password")
    fun requestForgotPassword(@RequestBody account: AccountDTO): ResponseEntity<String> {
        return ResponseEntity(authenticationService.requestForgotPassword(account), HttpStatus.OK)
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestBody account: AccountForgotPasswordDTO): ResponseEntity<String> {
        return ResponseEntity(authenticationService.forgotPassword(account), HttpStatus.OK)
    }

    @PostMapping("/update-firebase-token")
    fun updateFirebaseToken(request: HttpServletRequest, @RequestBody firebaseDeviceToken: FirebaseDeviceToken): ResponseEntity<String> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val userID = jwtBody.userID ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return ResponseEntity(deviceService.updateFirebaseToken(firebaseDeviceToken, userID), HttpStatus.OK)
    }
}