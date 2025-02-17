package com.pvt.auth_service.services

import com.pvt.auth_service.constants.RabbitMQ
import com.pvt.auth_service.models.dtos.RabbitMessageDTO
import com.pvt.auth_service.models.dtos.RecordLevelAccessPayloadDTO
import com.pvt.auth_service.models.dtos.asListUsersWithRecordLevelAccessEntity
import com.pvt.auth_service.models.dtos.asRecordLevelAccessEntity
import com.pvt.auth_service.models.entitys.*
import com.pvt.auth_service.publisher.RabbitMQProducer
import com.pvt.auth_service.repositories.*
import com.pvt.auth_service.utils.RecordLevelAccessUtils
import com.pvt.auth_service.utils.cache.DomainLevelAccessCache
import com.pvt.auth_service.utils.cache.RecordLevelAccessCache
import kotlinx.coroutines.*
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class AuthorizationServiceImpl(
    val recordLevelAccessRepository: RecordLevelAccessRepository,
    val usersWithRecordLevelAccessRepository: UsersWithRecordLevelAccessRepository,
    val rabbitMQProducer: RabbitMQProducer
): AuthorizationService {
    @Autowired
    private lateinit var domainLevelAccessRepository: DomainLevelAccessRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var roleDomainAccessMappingRepository: RoleDomainAccessMappingRepository

    @Autowired
    private lateinit var roleUserMappingRepository: RoleUserMappingRepository

    @Transactional
    private fun initDomainLevelAccesses(): List<DomainLevelAccessEntity> {
        val listRoleLabel = listOf(
            DomainLevelAccessEntity(accessContent = "/auth/logout-device", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/auth/logout-all-device", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/auth/devices", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/auth/change-password", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/auth/forgot-password", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/auth/request-forgot-password", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/auth/update-firebase-token", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/resources/upload/private/multi", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/resources/upload/public/multi", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/resources/upload/public/avatar", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/resources/[a-fA-F0-9\\\\-]+", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/resources/[a-fA-F0-9\\\\-]+/thumbnail", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/resources/[a-fA-F0-9\\\\-]+/download", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/friends", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/friends/request-add-friend", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/friends/channels/[a-fA-F0-9\\\\-]+/outside", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/friends/[a-fA-F0-9\\\\-]+/send-request-add-friend", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/friends/[a-fA-F0-9\\\\-]+/confirmation-add-friend", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/friends/[a-fA-F0-9\\\\-]+/unfriend", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/friends/[a-fA-F0-9\\\\-]+/cancel-request-add-friend", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/friends/[a-fA-F0-9\\\\-]+/reject-friend-request", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/friends/number-request-add-friend", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/friends/[a-fA-F0-9\\\\-]+/status", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/users", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/users/self", method = "PUT"),
            DomainLevelAccessEntity(accessContent = "/users/[a-fA-F0-9\\\\-]+", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/users/self", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/users/self/setting", method = "PUT"),
            DomainLevelAccessEntity(accessContent = "/channels", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/channels/group", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+/group", method = "PUT"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+/group/add-members", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+/group/remove-members", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+/group/my-role", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+/group/leave-group", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+/group/set-admin-role", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+/group/revoke-admin-role", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+/group/set-owner-role", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+/members", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/channels/[a-fA-F0-9\\\\-]+/video-call", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/messages/channels/[a-fA-F0-9\\\\-]+", method = "GET"),
            DomainLevelAccessEntity(accessContent = "/messages/channels/[a-fA-F0-9\\\\-]+", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/messages/[a-fA-F0-9\\\\-]+/reaction", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/messages/[a-fA-F0-9\\\\-]+/read-message", method = "POST"),
            DomainLevelAccessEntity(accessContent = "/messages/[a-fA-F0-9\\\\-]+", method = "PUT"),
            DomainLevelAccessEntity(accessContent = "/messages/[a-fA-F0-9\\\\-]+/for-all", method = "DELETE"),
            DomainLevelAccessEntity(accessContent = "/messages/[a-fA-F0-9\\\\-]+/for-owner", method = "DELETE")
        )

        val allDomain = domainLevelAccessRepository.findAll()
        val newListRoleLabel = mutableListOf<DomainLevelAccessEntity>()
        val allDomainMap = allDomain.associateBy { it.accessContent + it.method }

        for (roleLabel in listRoleLabel) {
            if (allDomainMap[roleLabel.accessContent + roleLabel.method] == null) {
                newListRoleLabel.add(roleLabel)
            }
        }

        domainLevelAccessRepository.saveAllAndFlush(newListRoleLabel)
        return domainLevelAccessRepository.findAll()
    }

    @Transactional
    private fun initRole(): RoleEntity {
        val role = RoleEntity(roleName = "DEFAULT", tenantCode = "MSC")
        return roleRepository.findByRoleNameAndTenantCodeAndAuthStatus(roleName = role.roleName, tenantCode = role.tenantCode).orElseGet {
            roleRepository.saveAndFlush(role)
        }
    }

    @Transactional
    private fun initRoleDomainAccessMapping(roleDomainAccessMappings: List<RoleDomainAccessMappingEntity>) {
        roleDomainAccessMappingRepository.deleteAll()
        roleDomainAccessMappingRepository.saveAllAndFlush(roleDomainAccessMappings)
    }

    @Transactional
    private fun findAllRoleUserMappingByUserID(userID: UUID): List<RoleUserMappingEntity> {
        return roleUserMappingRepository.findAllByUserIDAndAuthStatus(userID)
    }

    @Transactional
    private fun findAllRoleDomainAccessMappingByRoleIDs(authorizations :List<RoleUserMappingEntity>): List<RoleDomainAccessMappingEntity> {
        val roleIDs: List<UUID> = LinkedHashSet(authorizations.map { it.roleID }).toList()
        return roleDomainAccessMappingRepository.findAllByRoleIDsAndAuthStatus(roleIDs)
    }

    @Transactional
    private fun findAllRoleLabelByIDs(roleDomainAccessMappings: List<RoleDomainAccessMappingEntity>): List<DomainLevelAccessEntity> {
        val ids: List<UUID> = LinkedHashSet(roleDomainAccessMappings.map { it.domainLevelAccessID }).toList()
        return domainLevelAccessRepository.findAllByIDsAndAuthStatus(ids)
    }

    @Transactional
    private fun findAllDomainLevelAccessByUserID(userID: UUID): List<DomainLevelAccessEntity> {
        val roleUserMappings = findAllRoleUserMappingByUserID(userID)
        val roleDomainAccessMappings = findAllRoleDomainAccessMappingByRoleIDs(roleUserMappings)
        return findAllRoleLabelByIDs(roleDomainAccessMappings)
    }

    @Transactional
    private fun validationAuthorizationDomainLevel(userID: UUID, path: String, method: String) {
        val domainLevelAccesses = findAllDomainLevelAccessByUserID(userID)
        DomainLevelAccessCache.setCache(userID, domainLevelAccesses)
        val isValid = domainLevelAccesses.any { isMatchPath(path, it.accessContent) && method == it.method }
        if (!isValid) throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Permission denied!")
    }

    @Transactional
    private fun validationAuthorizationRecordLevel(userID: UUID, pathIgnoreParams: String, method: String) {
        val recordLevelAccess = recordLevelAccessRepository.findByAccessContentAndMethodAndAuthStatus(pathIgnoreParams, method).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }

        if (recordLevelAccess.recordStatus == "private") {
            usersWithRecordLevelAccessRepository.findByRecordLevelAccessIDAndUserIDAndAuthStatus(recordLevelAccess.id, userID).orElseThrow {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Permission denied!")
            }
        }

        RecordLevelAccessCache.setCache(userID, pathIgnoreParams, method)
    }

    private fun isMatchPath(path: String, pattern: String): Boolean {
        val regex = Regex("^${pattern}\$")
        return regex.matches(path)
    }

    private fun extractPathIgnoreParamsFromPath(path: String): String {
        return path.substringAfterLast("?")
    }

    @Transactional
    override fun setupRole() {
        val domainLevelAccesses = initDomainLevelAccesses()
        val role = initRole()

        var roleDomainAccessMappings = mutableListOf<RoleDomainAccessMappingEntity>()
        for (domainLevelAccess in domainLevelAccesses) {
            roleDomainAccessMappings.add(RoleDomainAccessMappingEntity(domainLevelAccessID = domainLevelAccess.id, roleID = role.id))
        }

        initRoleDomainAccessMapping(roleDomainAccessMappings)
    }

    @Transactional
    override fun validationDomainLevelAccess(userID: UUID, path: String, method: String) {
        val domainLevelAccessCache = DomainLevelAccessCache.getCache(userID)

        if (domainLevelAccessCache?.get(path + method) != true) {
            validationAuthorizationDomainLevel(userID, path, method)
        }
    }

    @Transactional
    override fun validationRecordLevelAccess(userID: UUID, path: String, method: String) {
        val shouldValidation = RecordLevelAccessUtils.shouldValidation(path, method)
        if (!shouldValidation) return

        val pathIgnoreParams = extractPathIgnoreParamsFromPath(path)
        val hasCache = RecordLevelAccessCache.hasCache(userID, pathIgnoreParams, method)
        if (!hasCache) {
            validationAuthorizationRecordLevel(userID, pathIgnoreParams, method)
        }
    }

    override fun createRecordLevelAccess(payload: RecordLevelAccessPayloadDTO) {
        val recordLevelAccess = recordLevelAccessRepository.findByAccessContentAndMethodAndAuthStatus(
            payload.accessContent,
            payload.method
        ).orElseGet {
            recordLevelAccessRepository.saveAndFlush(payload.asRecordLevelAccessEntity())
        }
        val newUserAccesses: MutableList<UsersWithRecordLevelAccessEntity> = mutableListOf()
        val userAccesses = payload.asListUsersWithRecordLevelAccessEntity(recordLevelAccess.id)
        val accessibleMembers = usersWithRecordLevelAccessRepository.findAllByRecordLevelAccessIDAndAuthStatus(recordLevelAccess.id)
        val accessibleMembersMap = accessibleMembers.associateBy { it.userID }
        for (userAccess in userAccesses) {
            accessibleMembersMap[userAccess.userID] ?: newUserAccesses.add(userAccess)
        }
        usersWithRecordLevelAccessRepository.saveAllAndFlush(newUserAccesses)
    }

    private fun revokeRecordLevelAccess(payload: RecordLevelAccessPayloadDTO) {
        val recordLevelAccess = recordLevelAccessRepository.findByAccessContentAndMethodAndAuthStatus(
            payload.accessContent,
            payload.method
        ).orElseThrow()

        val revokeAccessMembers = usersWithRecordLevelAccessRepository.findAllByUserIDs(payload.userAccessIDs, recordLevelAccess.id)
        usersWithRecordLevelAccessRepository.deleteAll(revokeAccessMembers)
    }

    @RabbitListener(queues = [RabbitMQ.Listener.MSCMN_CREATE_RECORD_LEVEL_ACCESS])
    @Transactional
    fun createRecordLevelAccesses(data: RabbitMessageDTO<List<RecordLevelAccessPayloadDTO>>) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val payloads = data.message!!
                for (payload in payloads) {
                    val job = launch { createRecordLevelAccess(payload) }
                    job.join()
                }
                rabbitMQProducer.sendMessage("Success", RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.callbackRoute())
            }
        } catch (e: Exception) {
            rabbitMQProducer.sendMessage("Compensation", RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.callbackRoute())
        }
    }

    @RabbitListener(queues = [RabbitMQ.Listener.MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS])
    @Transactional
    private fun revokeRecordLevelAccess(data: RabbitMessageDTO<List<RecordLevelAccessPayloadDTO>>) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val payloads = data.message!!
                for (payload in payloads) {
                    val job = launch { revokeRecordLevelAccess(payload) }
                    job.join()
                }
                rabbitMQProducer.sendMessage("Success", RabbitMQ.MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS.callbackRoute())
            }
        } catch (e: Exception) {
            rabbitMQProducer.sendMessage("Compensation", RabbitMQ.MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS.callbackRoute())
        }
    }
}