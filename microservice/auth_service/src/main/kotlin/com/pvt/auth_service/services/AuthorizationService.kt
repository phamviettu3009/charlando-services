package com.pvt.auth_service.services

import java.util.*

interface AuthorizationService {
    fun setupRole()
    fun validationDomainLevelAccess(userID: UUID, path: String, method: String)
    fun validationRecordLevelAccess(userID: UUID, path: String, method: String)
}