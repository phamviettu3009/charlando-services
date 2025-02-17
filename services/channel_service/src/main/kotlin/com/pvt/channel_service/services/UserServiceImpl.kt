package com.pvt.channel_service.services

import com.pvt.channel_service.constants.Friend
import com.pvt.channel_service.constants.RabbitMQ
import com.pvt.channel_service.constants.RealtimeEndpoint
import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.models.entitys.SettingEntity
import com.pvt.channel_service.models.entitys.UserEntity
import com.pvt.channel_service.publisher.RabbitMQProducer
import com.pvt.channel_service.repositories.ChannelRepository
import com.pvt.channel_service.repositories.FriendRepository
import com.pvt.channel_service.repositories.SettingRepository
import com.pvt.channel_service.repositories.UserRepository
import com.pvt.channel_service.utils.ChannelModifier
import com.pvt.channel_service.utils.converter.DateTimeConverter
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserServiceImpl(val userRepository: UserRepository, val settingRepository: SettingRepository): UserService {
    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    @Autowired
    private  lateinit var friendRepository: FriendRepository

    @Autowired
    private lateinit var channelRepository: ChannelRepository

    @RabbitListener(queues = [RabbitMQ.Listener.MSC_CREATE_RECORD_USER])
    private fun createUser(data: RabbitMessageDTO<UserDTO>) {
        try {
            val coverPhotoDefaults = listOf(
                "5694eaba-9bee-4c81-9912-a2790db7601e",
                "d9c685bc-379a-4fcd-b058-f3cf8d7e50b3",
                "0e7623de-4110-4585-a4b2-4be1bef948d8",
                "9194f06e-494d-4495-9825-9bc2d010ae19",
                "ad31b8fd-ed1a-4e85-ba27-9302326efa0f",
                "5f793f7c-59e5-403a-ad7c-ad6abf1bbae7",
                "b88e4a76-da36-4cf1-bead-a39d0675117f",
                "3feea77d-368d-4d95-9bc1-b9b3cac6b724",
                "832f4a42-bd24-4383-b8d5-9555bfb5d7a9",
                "359c7f58-5fad-4283-9517-58c720170f78"
            )

            val payload = data.message ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
            val user = payload.asUserEntity()
            user.email = user.fullName
            user.avatar = "empty"
            user.coverPhoto = coverPhotoDefaults.random()
            userRepository.save(user)
            createSetting(userID = user.id)

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

    override fun getUser(request: RequestDTO<Unit>): ExpandUserResponseDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val user = userRepository.findById(ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val setting = settingRepository.findByUserID(ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        return user.asExpandUserResponseDTO(setting.asSettingDTO())
    }

    override fun getUser(request: RequestDTO<UUID>): ExpandUserResponseDTO2 {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val userID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val owner = userRepository.findById(ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val user = userRepository.findById(userID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val setting = settingRepository.findByUserID(userID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val friendQuantity = friendRepository.countFriend(user.id).get()
        val channelEntity = channelRepository.findByOwnerIDAndUserID(owner.id, user.id).orElse(null)

        val friendEntity = friendRepository.findByUserIDAndFriendID(userID = owner.id, friendID = user.id).orElse(null)
        val relationshipStatus = friendEntity?.recordStatus ?: Friend.RecordStatus.UNFRIEND

        return user.asExpandUserResponseDTO2(
            relationshipStatus,
            friendQuantity.quantity,
            channelEntity?.id,
            setting.asSettingDTO()
        )
    }

    override fun updateUser(request: RequestDTO<UserUpdateRequestDTO>): ExpandUserResponseDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val payload = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val fullName = payload.fullName
        val avatar = payload.avatar
        val coverPhoto = payload.coverPhoto
        val gender = payload.gender
        val phone = payload.phone
        val email = payload.email
        val countryCode = payload.countryCode
        val languageCode = payload.languageCode
        val dob = payload.dob

        val owner = userRepository.findById(ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val setting = settingRepository.findByUserID(ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if (fullName != null && fullName.isNotEmpty()) {
            owner.fullName = fullName
        }
        if (avatar != null && avatar.isNotEmpty()) {
            owner.avatar = avatar
        }
        if (coverPhoto != null && coverPhoto.isNotEmpty()) {
            owner.coverPhoto = coverPhoto
        }
        if (gender != null && gender.isNotEmpty()) {
            owner.gender = gender
        }
        if (phone != null && phone.isNotEmpty()) {
            owner.phone = phone
        }
        if (email != null && email.isNotEmpty()) {
            owner.email = email
        }
        if (countryCode != null && countryCode.isNotEmpty()) {
            owner.countryCode = countryCode
        }
        if (languageCode != null && languageCode.isNotEmpty()) {
            owner.languageCode = languageCode
        }
        if (dob != null && dob.isNotEmpty()) {
            val date = DateTimeConverter.convertIsoStringToDate(dob)
            owner.dob = date
        }

        val updated = userRepository.saveAndFlush(owner)
        return updated.asExpandUserResponseDTO(setting.asSettingDTO())
    }

    override fun getUserByID(userID: UUID): UserResponseDTO {
        val user = userRepository.findById(userID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
        return user.asUserResponseDTO()
    }

    override fun updateSetting(request: RequestDTO<SettingDTO>): SettingDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val payload = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val settingRecord = settingRepository.findByUserID(ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        settingRecord.publicEmail = payload.publicEmail
        settingRecord.publicGender = payload.publicGender
        settingRecord.publicPhone = payload.publicPhone
        settingRecord.publicDob = payload.publicDob
        val settingSaved = settingRepository.saveAndFlush(settingRecord)
        return settingSaved.asSettingDTO()
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

    private fun createSetting(userID: UUID) {
        val setting = SettingEntity(
            userID = userID,
            publicEmail = false,
            publicDob = false,
            publicGender = false,
            publicPhone = false
        )

        settingRepository.save(setting)
    }
}