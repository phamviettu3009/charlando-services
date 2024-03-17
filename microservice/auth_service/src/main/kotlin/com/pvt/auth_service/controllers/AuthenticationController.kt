package com.pvt.auth_service.controllers

import com.pvt.auth_service.models.dtos.*
import com.pvt.auth_service.services.AuthenticationService
import com.pvt.auth_service.services.AuthorizationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController {
    @Autowired
    private lateinit var authenticationService: AuthenticationService

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
    fun getNewAccessToken(@RequestBody refreshToken: JwtDTO): ResponseEntity<JwtDTO> {
        return ResponseEntity(authenticationService.getNewAccessToken(refreshToken), HttpStatus.OK)
    }

    @PostMapping("/resend-verify-code")
    fun resendVerifyCode(@RequestBody account: AccountDTO): ResponseEntity<String> {
        return ResponseEntity(authenticationService.resendVerifyCode(account), HttpStatus.OK)
    }

    @PostMapping("/logout-device")
    fun logoutDevice(
        request: HttpServletRequest,
        @RequestParam(defaultValue = "") deviceID: String
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
}