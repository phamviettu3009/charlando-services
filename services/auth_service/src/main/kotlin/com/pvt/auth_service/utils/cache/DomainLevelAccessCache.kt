package com.pvt.auth_service.utils.cache

import com.pvt.auth_service.models.entitys.DomainLevelAccessEntity
import java.util.UUID

object DomainLevelAccessCache {
    private val domainLevelAccessCache: HashMap<String, Map<String, Boolean>> = hashMapOf()

    fun getCache(userID: UUID): Map<String, Boolean>? {
        return domainLevelAccessCache[userID.toString()]
    }

    fun setCache(userID: UUID, roleDomainAccessMappings: List<DomainLevelAccessEntity>) {
        removeCache(userID)
        val destinationRolesHashMap = roleDomainAccessMappings.associateBy ({ it.accessContent + it.method }) { true }
        domainLevelAccessCache[userID.toString()] = destinationRolesHashMap
    }

    private fun removeCache(userID: UUID) {
        domainLevelAccessCache.remove(userID.toString())
    }
}