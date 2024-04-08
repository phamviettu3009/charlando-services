package com.pvt.channel_service.services

import com.pvt.channel_service.constants.Friend
import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.models.entitys.FriendEntity
import com.pvt.channel_service.repositories.FriendRepository
import com.pvt.channel_service.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class FriendServiceImpl: FriendService {
    @Autowired
    private lateinit var friendRepository: FriendRepository

    @Autowired
    private lateinit var channelService: ChannelService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var memberService: MemberService

    private fun findByUserIDAndFriendID(userID: UUID, friendID: UUID): Optional<FriendEntity> {
        return friendRepository.findByUserIDAndFriendID(userID, friendID)
    }

    @Transactional
    override fun sendRequestAddFriend(request: RequestDTO<UUID>): Any {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED)
        val friendID = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val user = userService.findCMNUserByID(friendID) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (friendID == ownerID) throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val owner = findByUserIDAndFriendID(ownerID, friendID).or {
            val newRecord = FriendEntity(userID = ownerID, friendID = friendID, recordStatus = Friend.RecordStatus.FRIEND_REQUEST_SENT)
            val created = friendRepository.saveAndFlush(newRecord)
            Optional.ofNullable(created)
        }.get()

        if (owner.recordStatus == Friend.RecordStatus.UNFRIEND) {
            val owner = findByUserIDAndFriendID(ownerID, friendID).orElseThrow()
            owner.recordStatus = Friend.RecordStatus.FRIEND_REQUEST_SENT
            friendRepository.save(owner)
        }

        val friend = findByUserIDAndFriendID(friendID, ownerID).or {
            val newRecord = FriendEntity(userID = friendID, friendID = ownerID, recordStatus = Friend.RecordStatus.WAIT_FOR_CONFIRMATION)
            val created = friendRepository.saveAndFlush(newRecord)
            Optional.ofNullable(created)
        }.get()

        if (friend.recordStatus == Friend.RecordStatus.UNFRIEND) {
            val friend = findByUserIDAndFriendID(friendID, ownerID).orElseThrow()
            friend.recordStatus = Friend.RecordStatus.WAIT_FOR_CONFIRMATION
            friendRepository.save(friend)
        }

        return mapOf("userID" to user.id, "status" to Friend.RecordStatus.WAIT_FOR_CONFIRMATION)
    }

    @Transactional
    override fun unFriend(request: RequestDTO<UUID>): Any {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED)
        val friendID = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val user = userService.findCMNUserByID(friendID) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (friendID == ownerID) throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val owner = friendRepository.findByUserIDAndFriendIDAndRecordStatus(
            ownerID,
            friendID,
            Friend.RecordStatus.FRIEND
        ).orElseThrow { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        owner.recordStatus = Friend.RecordStatus.UNFRIEND

        val friend = friendRepository.findByUserIDAndFriendIDAndRecordStatus(
            friendID,
            ownerID,
            Friend.RecordStatus.FRIEND
        ).orElseThrow { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        friend.recordStatus = Friend.RecordStatus.UNFRIEND

        friendRepository.saveAll(listOf(owner, friend))
        return mapOf("userID" to user.id, "status" to Friend.RecordStatus.UNFRIEND)
    }

    @Transactional
    override fun confirmationAddFriend(request: RequestDTO<UUID>): Any {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED)
        val friendID = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val user = userService.findCMNUserByID(friendID) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (friendID == ownerID) throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val owner = friendRepository.findByUserIDAndFriendIDAndRecordStatus(
            ownerID,
            friendID,
            Friend.RecordStatus.WAIT_FOR_CONFIRMATION
        ).orElseThrow { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        owner.recordStatus = Friend.RecordStatus.FRIEND

        val friend = friendRepository.findByUserIDAndFriendIDAndRecordStatus(
            friendID,
            ownerID,
            Friend.RecordStatus.FRIEND_REQUEST_SENT
        ).orElseThrow { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        friend.recordStatus = Friend.RecordStatus.FRIEND

        friendRepository.saveAll(listOf(owner, friend))
        channelService.createSingleChannel(ownerID, friendID)
        return mapOf("userID" to user.id, "status" to Friend.RecordStatus.FRIEND)
    }

    override fun cancelRequestAddFriend(request: RequestDTO<UUID>): Any {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED)
        val friendID = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val user = userService.findCMNUserByID(friendID) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (friendID == ownerID) throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val owner = friendRepository.findByUserIDAndFriendIDAndRecordStatus(
            ownerID,
            friendID,
            Friend.RecordStatus.FRIEND_REQUEST_SENT
        ).orElseThrow { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        owner.recordStatus = Friend.RecordStatus.UNFRIEND

        val friend = friendRepository.findByUserIDAndFriendIDAndRecordStatus(
            friendID,
            ownerID,
            Friend.RecordStatus.WAIT_FOR_CONFIRMATION
        ).orElseThrow { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        friend.recordStatus = Friend.RecordStatus.UNFRIEND

        friendRepository.saveAll(listOf(owner, friend))
        return mapOf("userID" to user.id, "status" to Friend.RecordStatus.UNFRIEND)
    }

    override fun rejectFriendRequest(request: RequestDTO<UUID>): Any {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED)
        val friendID = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val user = userService.findCMNUserByID(friendID) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (friendID == ownerID) throw ResponseStatusException(HttpStatus.BAD_REQUEST)

        val owner = friendRepository.findByUserIDAndFriendIDAndRecordStatus(
            ownerID,
            friendID,
            Friend.RecordStatus.WAIT_FOR_CONFIRMATION
        ).orElseThrow { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        owner.recordStatus = Friend.RecordStatus.UNFRIEND

        val friend = friendRepository.findByUserIDAndFriendIDAndRecordStatus(
            friendID,
            ownerID,
            Friend.RecordStatus.FRIEND_REQUEST_SENT
        ).orElseThrow { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        friend.recordStatus = Friend.RecordStatus.UNFRIEND

        friendRepository.saveAll(listOf(owner, friend))
        return mapOf("userID" to user.id, "status" to Friend.RecordStatus.UNFRIEND)
    }

    override fun getFriends(request: RequestDTO<ListRequestDTO>): ListResponseDTO<UserResponseDTO> {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val listRequestParams = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (listRequestParams.page == 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val page = listRequestParams.page - 1
        val sizePerPage = listRequestParams.sizePerPage
        val pageRequest = PageRequest.of(page, sizePerPage)

        val users = userRepository.findAllFriendByUserIDAndKeyword(ownerID, keyword = listRequestParams.keyword, pageable = pageRequest)
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

    override fun getFriendsOutsideChannel(request: RequestDTO<ListRequestDTO>): ListResponseDTO<UserResponseDTO> {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val listRequestParams = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (listRequestParams.page == 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val page = listRequestParams.page - 1
        val sizePerPage = listRequestParams.sizePerPage
        val pageRequest = PageRequest.of(page, sizePerPage)

        val member = memberService.findByChannelIDAndUserID(channelID, ownerID)
        val users = userRepository.findAllFriendOutsideChannelByChannelIDAndUserIDAndKeyword(
            member.channelID,
            ownerID,
            keyword = listRequestParams.keyword,
            pageable = pageRequest
        )
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

    override fun getListRequestAddFriend(request: RequestDTO<ListRequestDTO>): ListResponseDTO<UserResponseDTO> {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val listRequestParams = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (listRequestParams.page == 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val page = listRequestParams.page - 1
        val sizePerPage = listRequestParams.sizePerPage
        val pageRequest = PageRequest.of(page, sizePerPage)

        val users = userRepository.findAllFriendByUserIDAndRecordStatusKeyword(
            ownerID,
            Friend.RecordStatus.WAIT_FOR_CONFIRMATION,
            keyword = listRequestParams.keyword,
            pageable = pageRequest
        )

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
}