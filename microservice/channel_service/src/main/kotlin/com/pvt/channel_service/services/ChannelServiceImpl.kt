package com.pvt.channel_service.services

import com.pvt.channel_service.constants.Channel
import com.pvt.channel_service.constants.Member
import com.pvt.channel_service.constants.RabbitMQ
import com.pvt.channel_service.constants.RealtimeEndpoint
import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.models.entitys.ChannelEntity
import com.pvt.channel_service.models.entitys.MemberEntity
import com.pvt.channel_service.publisher.RabbitMQProducer
import com.pvt.channel_service.repositories.*
import com.pvt.channel_service.utils.ChannelModifier
import com.pvt.channel_service.utils.extension.asUUID
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ChannelServiceImpl(
    val memberRepository: MemberRepository,
    val rabbitMQProducer: RabbitMQProducer
): ChannelService {
    @Autowired
    private lateinit var channelRepository: ChannelRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var messageRepository: MessageRepository

    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var messageReadersRepository: MessageReadersRepository

    @Autowired
    private lateinit var userService: UserService

    @Transactional
    private fun putMessageCreateRecordLevelAccesses(payloads: List<RecordLevelAccessPayloadDTO>) {
        rabbitMQProducer.sendAndCallbackMessage<String>(
            payloads,
            RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.route(),
            RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.callbackQueue()
        )
    }

    @Transactional
    private fun putMessageRevokeRecordLevelAccesses(payloads: List<RecordLevelAccessPayloadDTO>) {
        rabbitMQProducer.sendAndCallbackMessage<String>(
            payloads,
            RabbitMQ.MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS.route(),
            RabbitMQ.MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS.callbackQueue()
        )
    }

    @Transactional
    private fun createRecordLevelAccessForSingleChannel(channelID: UUID, ownerID: UUID, userAccessIDs: List<UUID>) {
        val recordLevelAccessGetChannel = RecordLevelAccessPayloadDTO(
            accessContent = "/channel/${channelID}",
            method = "GET",
            recordStatus = "private",
            ownerID = ownerID,
            userAccessIDs = userAccessIDs
        )

        val recordLevelAccessGetMessagesByChannel = RecordLevelAccessPayloadDTO(
            accessContent = "/message/channel/${channelID}",
            method = "GET",
            recordStatus = "private",
            ownerID = ownerID,
            userAccessIDs = userAccessIDs
        )

        val recordLevelAccessSendMessage = RecordLevelAccessPayloadDTO(
            accessContent = "/message/channel/${channelID}",
            method = "POST",
            recordStatus = "private",
            ownerID = ownerID,
            userAccessIDs = userAccessIDs
        )

        putMessageCreateRecordLevelAccesses(listOf(
            recordLevelAccessGetChannel,
            recordLevelAccessGetMessagesByChannel,
            recordLevelAccessSendMessage
        ))
    }

    @Transactional
    private fun createRecordLevelAccessForGroupChannel(chanelID: UUID, ownerID: UUID, userAccessIDs: List<UUID>) {
        createRecordLevelAccessForSingleChannel(chanelID, ownerID, userAccessIDs)

        val recordLevelAccessUpdateChannelGroup = RecordLevelAccessPayloadDTO(
            accessContent = "/channel/group/${chanelID}",
            method = "PUT",
            recordStatus = "private",
            ownerID = ownerID,
            userAccessIDs = userAccessIDs
        )

        val recordLevelAccessAddMembers = RecordLevelAccessPayloadDTO(
            accessContent = "/channel/group/${chanelID}/add-members",
            method = "POST",
            recordStatus = "private",
            ownerID = ownerID,
            userAccessIDs = userAccessIDs
        )

        val recordLevelAccessRemoveMembers = RecordLevelAccessPayloadDTO(
            accessContent = "/channel/group/${chanelID}/remove-members",
            method = "POST",
            recordStatus = "private",
            ownerID = ownerID,
            userAccessIDs = userAccessIDs
        )

        putMessageCreateRecordLevelAccesses(listOf(
            recordLevelAccessUpdateChannelGroup,
            recordLevelAccessAddMembers,
            recordLevelAccessRemoveMembers
        ))
    }

    @Transactional
    private fun revokeAccessChannelForMembers(channelID: UUID, userAccessIDs: List<UUID>) {
        val revokeRecordLevelAccessGetChannel = RecordLevelAccessPayloadDTO(
            accessContent = "/channel/${channelID}",
            method = "GET",
            recordStatus = "private",
            ownerID = null,
            userAccessIDs = userAccessIDs
        )

        val revokeRecordLevelAccessGetMessagesByChannel = RecordLevelAccessPayloadDTO(
            accessContent = "/message/channel/${channelID}",
            method = "GET",
            recordStatus = "private",
            ownerID = null,
            userAccessIDs = userAccessIDs
        )

        val revokeRecordLevelAccessSendMessage = RecordLevelAccessPayloadDTO(
            accessContent = "/message/channel/${channelID}",
            method = "POST",
            recordStatus = "private",
            ownerID = null,
            userAccessIDs = userAccessIDs
        )

        val revokeRecordLevelAccessUpdateChannelGroup = RecordLevelAccessPayloadDTO(
            accessContent = "/channel/group/${channelID}",
            method = "PUT",
            recordStatus = "private",
            ownerID = null,
            userAccessIDs = userAccessIDs
        )

        val revokeRecordLevelAccessAddMembers = RecordLevelAccessPayloadDTO(
            accessContent = "/channel/group/${channelID}/add-members",
            method = "POST",
            recordStatus = "private",
            ownerID = null,
            userAccessIDs = userAccessIDs
        )

        val revokeRecordLevelAccessRemoveMembers = RecordLevelAccessPayloadDTO(
            accessContent = "/channel/group/${channelID}/remove-members",
            method = "POST",
            recordStatus = "private",
            ownerID = null,
            userAccessIDs = userAccessIDs
        )

        putMessageRevokeRecordLevelAccesses(listOf(
            revokeRecordLevelAccessGetChannel,
            revokeRecordLevelAccessGetMessagesByChannel,
            revokeRecordLevelAccessSendMessage,
            revokeRecordLevelAccessUpdateChannelGroup,
            revokeRecordLevelAccessAddMembers,
            revokeRecordLevelAccessRemoveMembers
        ))
    }

    @Transactional
    private fun convertResponseChannelDTO(channel: ChannelEntity, ownerID: UUID): ResponseChannelDTO {
        var membersInChannel: List<MemberInChannelDTO> = channelRepository.findAllByChannelIDs(
            ids = listOf(channel.id)
        )
        val lastMessages = messageRepository.findAlLastMessagesByChannelIDs(listOf(channel.id)).map { it.asResponseShortenMessageDTO(ownerID) }
        val messageReaderModifierHashMap = getMessageReaders(lastMessages.map { it.id })
        val ownerInChannel = memberRepository.findAllByChannelIDsAndUserID(listOf(channel.id), ownerID)
        val channelModifierHashMap = ChannelModifier.getChannelModifierMap(membersInChannel, ownerID)
        val messageModifierHashMap = ChannelModifier.getMessageModifierMap(lastMessages)
        val unreadCounterModifierHashMap = ChannelModifier.getUnreadCounterModifierMap(ownerInChannel)
        return channel.asResponseChannelDTO(channelModifierHashMap, messageModifierHashMap, messageReaderModifierHashMap, unreadCounterModifierHashMap, ownerID)
    }

    private fun sendRealtimeChannel(channelEntity: ChannelEntity) {
        val membersInChannel = memberService.findAllByChannelID(channelEntity.id)
        val userIDs = membersInChannel.map { it.userID }

        for (userID in userIDs) {
            val payload = convertResponseChannelDTO(channelEntity, userID)
            val endpoint = RealtimeEndpoint.CHANNEL_BY_USER + userID
            val realtimeMessage = RealtimeMessageDTO(payload, endpoint, userID)
            rabbitMQProducer.sendMessage(realtimeMessage, RabbitMQ.MSCMN_SEND_REALTIME_MESSAGE.route())
        }
    }

    private fun sendRealtimeRemoveChannel(channelID: UUID, userIDs: List<UUID>) {
        for (userID in userIDs) {
            val endpoint = RealtimeEndpoint.DELETE_CHANNEL_BY_USER + userID
            val realtimeMessage = RealtimeMessageDTO(channelID, endpoint, userID)
            rabbitMQProducer.sendMessage(realtimeMessage, RabbitMQ.MSCMN_SEND_REALTIME_MESSAGE.route())
        }
    }

    @Transactional
    override fun createSingleChannel(ownerID: UUID, userID: UUID): ChannelEntity? {
        val owner = userRepository.findById(ownerID).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND) }
        val friend = userRepository.findById(userID).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND) }

        val oldChannel = channelRepository.findByOwnerIDAndUserID(ownerID, userID).orElse(null)
        if (oldChannel != null) return null

        val newChannelRecord = ChannelEntity(makerID = ownerID, lastMessageTime = Date())
        val channel = channelRepository.saveAndFlush(newChannelRecord)
        val members = listOf(
            MemberEntity(channelID = channel.id, userID = owner.id),
            MemberEntity(channelID = channel.id, userID = friend.id)
        )
        memberRepository.saveAllAndFlush(members)
        createRecordLevelAccessForSingleChannel(channel.id, ownerID, listOf(friend.id))
        sendRealtimeChannel(channel)
        return channel
    }

    @Transactional
    override fun getChannel(channelID: UUID): ChannelEntity {
        return channelRepository.findByIdAndAuthStatus(channelID).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @Transactional
    override fun getChannel(request: RequestDTO<Unit>): ResponseChannelDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channel = channelRepository.findById(channelID).orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST) }
        return convertResponseChannelDTO(channel, ownerID)
    }

    @Transactional
    override fun getChannel(channelID: UUID, ownerID: UUID): ResponseChannelDTO {
        val channel = channelRepository.findById(channelID).orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST) }
        return convertResponseChannelDTO(channel, ownerID)
    }

    @Transactional
    override fun getChannels(request: RequestDTO<ListRequestDTO>): ListResponseDTO<ResponseChannelDTO> {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val listRequestParams = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (listRequestParams.page == 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val page = listRequestParams.page - 1
        val sizePerPage = listRequestParams.sizePerPage
        val pageRequest = PageRequest.of(page, sizePerPage)

        val channels = channelRepository.findAllByUserIDAndKeyword(userID = ownerID, keyword = listRequestParams.keyword, pageable = pageRequest)
        val channelIDs = channels.content.map { it.id }
        val meta = Meta(
            totalElements = channels.totalElements,
            totalPages = channels.totalPages,
            sizePerPage = channels.pageable.pageSize,
            currentPage = channels.pageable.pageNumber + 1,
            numberOfElements = channels.numberOfElements,
            last = channels.isLast
        )

        val membersInChannel: List<MemberInChannelDTO> = channelRepository.findAllByChannelIDs(ids = channelIDs)
        val lastMessages = messageRepository.findAlLastMessagesByChannelIDs(channelIDs).map { it.asResponseShortenMessageDTO(ownerID) }
        val messageReaderModifierHashMap = getMessageReaders(lastMessages.map { it.id })
        val ownerInChannel = memberRepository.findAllByChannelIDsAndUserID(channelIDs, ownerID)
        val channelModifierHashMap = ChannelModifier.getChannelModifierMap(membersInChannel, ownerID)
        val messageModifierHashMap = ChannelModifier.getMessageModifierMap(lastMessages)
        val unreadCounterModifierHashMap = ChannelModifier.getUnreadCounterModifierMap(ownerInChannel)
        return ListResponseDTO(
            channels.content.map { it.asResponseChannelDTO(
                channelModifierHashMap,
                messageModifierHashMap,
                messageReaderModifierHashMap,
                unreadCounterModifierHashMap,
                ownerID
            )},
            meta
        )
    }

    @Transactional
    override fun createGroupChannel(request: RequestDTO<GroupChannelRequestDTO>): ResponseChannelDTO {
        val ownerID = request.jwtBody.userID!!
        val memberIDs = request.payload?.memberIDs ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val groupName = request.payload.groupName

        val newChannelRecord = ChannelEntity(
            type = Channel.Type.GROUP_TYPE,
            makerID = ownerID,
            name = groupName,
            memberLimit = Channel.MemberLimit.GROUP,
            lastMessageTime = Date()
        )

        val channel = channelRepository.saveAndFlush(newChannelRecord)
        val users = userRepository.findByIDs(memberIDs.map { it.asUUID() })
        val userIDs = users.map { it.id }
        val ownerMember = MemberEntity(userID = ownerID, channelID = channel.id, role = Member.Role.OWNER)
        memberRepository.saveAllAndFlush(users.map { it.toNewRecordMemberEntity(channelID = channel.id) } + ownerMember)
        createRecordLevelAccessForGroupChannel(channel.id, ownerID, userIDs)
        sendRealtimeChannel(channel)
        return convertResponseChannelDTO(channel, ownerID)
    }

    @Transactional
    override fun updateGroupChannel(request: RequestDTO<GroupChannelUpdateRequestDTO>): ResponseChannelDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val groupName = request.payload?.name
        val groupAvatar = request.payload?.avatar

        val channel = channelRepository.findByIdAndTypeAndAuthStatus(channelID, Channel.Type.GROUP_TYPE).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        memberRepository.findByChannelIDAndUserID(channelID, ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if (groupName != null && groupName.isNotEmpty()) {
            channel.name = groupName
        }
        if (groupAvatar != null && groupAvatar.isNotEmpty()) {
            channel.avatar = groupAvatar
        }
        val updated = channelRepository.saveAndFlush(channel)
        sendRealtimeChannel(updated)
        return convertResponseChannelDTO(updated, ownerID)
    }

    @Transactional
    override fun addMembersToGroupChannel(request: RequestDTO<GroupChannelMembers>): ResponseChannelDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val memberIDs = request.payload?.memberIDs ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val channel = channelRepository.findByIdAndTypeAndAuthStatus(channelID, Channel.Type.GROUP_TYPE).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        memberRepository.findByChannelIDAndUserID(channelID, ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val users = userRepository.findByIDs(memberIDs.map { it.asUUID() })
        memberRepository.saveAllAndFlush(users.map { it.toNewRecordMemberEntity(channelID = channel.id) })
        val userIDs = users.map { it.id }
        createRecordLevelAccessForGroupChannel(channel.id, ownerID, userIDs)
        sendRealtimeChannel(channel)
        return convertResponseChannelDTO(channel, ownerID)
    }

    @Transactional
    override fun removeMembersInGroupChannel(request: RequestDTO<GroupChannelMembers>): ResponseChannelDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val memberIDs = request.payload?.memberIDs ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val channel = channelRepository.findByIdAndTypeAndAuthStatus(channelID, Channel.Type.GROUP_TYPE).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val owner = memberRepository.findByChannelIDAndUserID(channelID, ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if (!listOf(Member.Role.ADMIN, Member.Role.OWNER).contains(owner.role)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val users = userRepository.findByIDs(memberIDs.map { it.asUUID() })
        val membersInGroup = memberRepository.findAllByChannelIDAndUserIDs(channel.id, users.map { it.id })
        val revokeMemberIDs = membersInGroup.map { it.userID }
        memberRepository.deleteAll(membersInGroup)
        revokeAccessChannelForMembers(channel.id, revokeMemberIDs)
        sendRealtimeRemoveChannel(channel.id, revokeMemberIDs)
        return convertResponseChannelDTO(channel, ownerID)
    }

    override fun leaveGroupChannel(request: RequestDTO<Unit>): Any {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val channel = channelRepository.findByIdAndTypeAndAuthStatus(channelID, Channel.Type.GROUP_TYPE).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
        val membersInGroup = memberRepository.findAllByChannelIDAndUserIDs(channel.id, listOf(ownerID))
        memberRepository.deleteAll(membersInGroup)
        revokeAccessChannelForMembers(channel.id, listOf(ownerID))
        sendRealtimeRemoveChannel(channel.id, listOf(ownerID))
        revokeAccessChannelForMembers(channel.id, listOf(ownerID))
        return "successful"
    }

    override fun getMessageReaders(messageIDs: List<UUID>): MutableMap<String, MutableList<AvatarDTO>> {
        val result: MutableMap<String, MutableList<AvatarDTO>> = mutableMapOf()
        val messageReaderRecords = messageReadersRepository.findAllByMessageIDs(messageIDs)
        val userIDs = messageReaderRecords.map { it.userID }
        val userRecord = userService.findAllByIDs(userIDs)
        val userRecordHashMap = userRecord.associateBy { it.id }

        for (messageReaderRecord in messageReaderRecords) {
            val channelIdKey = messageReaderRecord.channelID.toString()
            val avatar = userRecordHashMap[messageReaderRecord.userID]?.avatar ?: continue
            val avatarRecord = AvatarDTO(
                source = avatar.asUUID(),
                userID = messageReaderRecord.userID
            )
            if (result[channelIdKey] == null) {
                result[channelIdKey] = mutableListOf(avatarRecord)
            } else {
                result[channelIdKey]?.add(avatarRecord)
            }
        }

        return result
    }

    override fun getRole(request: RequestDTO<Unit>): Any {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        channelRepository.findByIdAndTypeAndAuthStatus(channelID, Channel.Type.GROUP_TYPE).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val member = memberRepository.findByChannelIDAndUserID(channelID, ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        return mapOf("userID" to member.userID, "role" to member.role)
    }

    @Transactional
    override fun setAdminRole(request: RequestDTO<GroupChannelMembers>): Any {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val memberIDs = request.payload?.memberIDs ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        channelRepository.findByIdAndTypeAndAuthStatus(channelID, Channel.Type.GROUP_TYPE).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val owner = memberRepository.findByChannelIDAndUserID(channelID, ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if (!listOf(Member.Role.ADMIN, Member.Role.OWNER).contains(owner.role)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val users = userRepository.findByIDs(memberIDs.map { it.asUUID() })
        val membersInGroup = memberRepository.findAllByChannelIDAndUserIDs(channelID, users.map { it.id })
        val membersUpdate = membersInGroup.filter { it.role == Member.Role.MEMBER }.map {
            it.role = Member.Role.ADMIN
            it
        }
        memberRepository.saveAllAndFlush(membersUpdate)

        return "successful"
    }

    override fun revokeAdminRole(request: RequestDTO<GroupChannelMembers>): Any {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val memberIDs = request.payload?.memberIDs ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        channelRepository.findByIdAndTypeAndAuthStatus(channelID, Channel.Type.GROUP_TYPE).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val owner = memberRepository.findByChannelIDAndUserID(channelID, ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if (!listOf(Member.Role.ADMIN, Member.Role.OWNER).contains(owner.role)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val users = userRepository.findByIDs(memberIDs.map { it.asUUID() })
        val membersInGroup = memberRepository.findAllByChannelIDAndUserIDs(channelID, users.map { it.id })
        val membersUpdate = membersInGroup.filter { it.role == Member.Role.ADMIN }.map {
            it.role = Member.Role.MEMBER
            it
        }
        memberRepository.saveAllAndFlush(membersUpdate)

        return "successful"
    }

    @Transactional
    override fun setOwnerRole(request: RequestDTO<GroupChannelMember>): Any {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val memberID = request.payload?.memberID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        channelRepository.findByIdAndTypeAndAuthStatus(channelID, Channel.Type.GROUP_TYPE).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val owner = memberRepository.findByChannelIDAndUserID(channelID, ownerID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if (owner.role != Member.Role.OWNER) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val user = userRepository.findById(memberID.asUUID()).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val memberInGroup = memberRepository.findByChannelIDAndUserID(channelID, user.id).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        memberInGroup.role = Member.Role.OWNER
        owner.role = Member.Role.ADMIN

        memberRepository.saveAllAndFlush(listOf(owner, memberInGroup))

        return "successful"
    }

    @RabbitListener(queues = [RabbitMQ.Listener.MSC_TYPING])
    private fun typing(data: RabbitMessageDTO<TypingDTO>) {
        try {
            val payload = data.message ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
            val users = memberRepository.findAllUserByChannelID(payload.channelID).map { it.asUserResponseDTO() }
            rabbitMQProducer.sendMessage(users, RabbitMQ.MSC_TYPING.callbackRoute())
        } catch (e: Exception) {

        }
    }
}
