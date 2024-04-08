package com.pvt.channel_service.services

import com.pvt.channel_service.constants.Friend
import com.pvt.channel_service.constants.RabbitMQ
import com.pvt.channel_service.constants.RealtimeEndpoint
import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.models.entitys.FriendEntity
import com.pvt.channel_service.models.entitys.UserEntity
import com.pvt.channel_service.publisher.RabbitMQProducer
import com.pvt.channel_service.repositories.ChannelRepository
import com.pvt.channel_service.repositories.FriendRepository
import com.pvt.channel_service.repositories.UserRepository
import com.pvt.channel_service.utils.ChannelModifier
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserServiceImpl(val userRepository: UserRepository): UserService {
    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    @Autowired
    private  lateinit var friendRepository: FriendRepository

    @Autowired
    private lateinit var channelRepository: ChannelRepository

    @RabbitListener(queues = [RabbitMQ.Listener.MSC_CREATE_RECORD_USER])
    private fun createUser(data: RabbitMessageDTO<UserDTO>) {
        try {
            val payload = data.message ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
            val user = payload.asUserEntity()
            user.email = user.fullName
            userRepository.save(user)
            rabbitMQProducer.sendMessage("Success", RabbitMQ.MSC_CREATE_RECORD_USER.callbackRoute())
        } catch (e: Exception) {
            rabbitMQProducer.sendMessage("Compensation", RabbitMQ.MSC_CREATE_RECORD_USER.callbackRoute())
        }
    }

    @RabbitListener(queues = [RabbitMQ.Listener.MSC_UPDATE_RECORD_USER])
    private fun updateUser(data: RabbitMessageDTO<UserDTO>) {
        try {
            val payload = data.message ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
            val user = payload.asUserEntity()
            userRepository.save(user)
            rabbitMQProducer.sendMessage("Success", RabbitMQ.MSC_UPDATE_RECORD_USER.callbackRoute())
        } catch (e: Exception) {
            rabbitMQProducer.sendMessage("Compensation", RabbitMQ.MSC_UPDATE_RECORD_USER.callbackRoute())
        }
    }

    @RabbitListener(queues = [RabbitMQ.Listener.MSC_UPDATE_ONLINE_STATUS_RECORD_USER])
    private fun updateOnlineStatusUser(data: RabbitMessageDTO<OnlineStatusDTO>) {
        try {
            val payload = data.message ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
            val user = userRepository.findById(payload.userID).orElseThrow()
            user.online = payload.online
            userRepository.saveAndFlush(user)
            sendRealtimeOnlineStatusChannel(payload.userID)
        } catch (_: Exception) {

        }
    }

    override fun findCMNUserByID(userID: UUID): UserDTO? {
        rabbitMQProducer.sendMessage(userID, RabbitMQ.MSCMN_GET_USER_BY_ID.route())
        val messageResponse = rabbitMQProducer.receiveUserMessage(RabbitMQ.MSCMN_GET_USER_BY_ID.callbackQueue())
        return messageResponse.message
    }

    override fun findAllByIDs(userIDs: List<UUID>): List<UserEntity> {
        return userRepository.findByIDs(userIDs)
    }

    override fun getUsers(request: RequestDTO<ListRequestDTO>): ListResponseDTO<UserResponseDTO> {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val listRequestParams = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (listRequestParams.page == 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val page = listRequestParams.page - 1
        val sizePerPage = listRequestParams.sizePerPage
        val pageRequest = PageRequest.of(page, sizePerPage)

        val users = userRepository.findAllByUserIDAndKeyword(ownerID, keyword = listRequestParams.keyword, pageable = pageRequest)
        val meta = Meta(
            totalElements = users.totalElements,
            totalPages = users.totalPages,
            sizePerPage = users.pageable.pageSize,
            currentPage = users.pageable.pageNumber + 1,
            numberOfElements = users.numberOfElements,
            last = users.isLast
        )
        return ListResponseDTO(users.content.map { it.asUserResponseDTO() }, meta)
    }

    override fun getUser(request: RequestDTO<Unit>): UserResponseDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val user = userRepository.findById(ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
        return user.asUserResponseDTO()
    }

    override fun getUsersInChannel(request: RequestDTO<ListRequestDTO>): ListResponseDTO<MemberResponseDTO> {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val listRequestParams = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (listRequestParams.page == 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val page = listRequestParams.page - 1
        val sizePerPage = listRequestParams.sizePerPage
        val pageRequest = PageRequest.of(page, sizePerPage)

        val users = userRepository.findAllUserByChannelID(channelID = channelID, pageable = pageRequest)
        val meta = Meta(
            totalElements = users.totalElements,
            totalPages = users.totalPages,
            sizePerPage = users.pageable.pageSize,
            currentPage = users.pageable.pageNumber + 1,
            numberOfElements = users.numberOfElements,
            last = users.isLast
        )
        return ListResponseDTO(users.content.map { it }, meta)
    }

    override fun getUserInfo(request: RequestDTO<Unit>): UserInfoResponseDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val userID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val owner = userRepository.findById(ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val user = userRepository.findById(userID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val friendQuantity = friendRepository.countFriend(user.id).get()
        val channelEntity = channelRepository.findByOwnerIDAndUserID(owner.id, user.id).orElse(null)

        val friendEntity = friendRepository.findByUserIDAndFriendID(userID = owner.id, friendID = user.id).orElse(null)
        val relationshipStatus = friendEntity?.recordStatus ?: Friend.RecordStatus.UNFRIEND

        return user.asUserInfoResponseDTO(
            relationshipStatus,
            friendQuantity.quantity,
            channelEntity?.id
        )
    }

    override fun updateUser(request: RequestDTO<UserUpdateRequestDTO>): UserResponseDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val payload = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val fullName = payload.fullName
        val avatar = payload.avatar

        val owner = userRepository.findById(ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if (fullName != null && fullName.isNotEmpty()) {
            owner.fullName = fullName
        }
        if (avatar != null && avatar.isNotEmpty()) {
            owner.avatar = avatar
        }

        val updated = userRepository.saveAndFlush(owner)
        return updated.asUserResponseDTO()
    }

    private fun sendRealtimeOnlineStatusChannel(userID: UUID) {
        val page = 0
        val sizePerPage = 50
        val pageRequest = PageRequest.of(page, sizePerPage)
        val channels = channelRepository.findAllByUserIDAndKeyword(userID = userID, keyword = "", pageable = pageRequest)
        val channelIDs = channels.content.map { it.id }
        val membersInChannel: List<MemberInChannelDTO> = channelRepository.findAllByChannelIDs(ids = channelIDs)
        val channelModifierHashMap = ChannelModifier.getChannelOnlineStatusModifierMap(membersInChannel, userID)

        for (member in membersInChannel) {
            if (member.userID == userID) { continue }
            val onlineStatus: Boolean = channelModifierHashMap[member.channelID.toString()] ?: false
            val payload = mapOf("channelID" to member.channelID, "online" to onlineStatus)
            val endpoint = RealtimeEndpoint.CHANNEL_ONLINE_STATUS_BY_USER + member.userID
            val realtimeMessage = RealtimeMessageDTO(payload, endpoint, member.userID)
            rabbitMQProducer.sendMessage(realtimeMessage, RabbitMQ.MSCMN_SEND_REALTIME_MESSAGE.route())
        }
    }
}