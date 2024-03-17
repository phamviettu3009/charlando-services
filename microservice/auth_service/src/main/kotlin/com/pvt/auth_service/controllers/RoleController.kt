package com.pvt.auth_service.controllers

import com.pvt.auth_service.services.AuthorizationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/role")
class RoleController {
    @Autowired
    private lateinit var authorizationService: AuthorizationService

    @PostMapping("/setup-role")
    fun setupRole() {
        authorizationService.setupRole()
    }
}