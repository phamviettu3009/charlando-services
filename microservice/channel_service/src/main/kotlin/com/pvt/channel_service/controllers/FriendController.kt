package com.pvt.channel_service.controllers

import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.services.FriendService
import com.pvt.channel_service.utils.extension.asUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/friend")
class FriendController {
    @Autowired
    private lateinit var fiendService: FriendService

    @PostMapping("/send-request-add-friend/{id}")
    fun sendRequestAddFriend(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, id.asUUID())
        return ResponseEntity(fiendService.sendRequestAddFriend(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/unfriend/{id}")
    fun unfriend(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, id.asUUID())
        return ResponseEntity(fiendService.unFriend(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/confirmation-add-friend/{id}")
    fun confirmationAddFriend(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, id.asUUID())
        return ResponseEntity(fiendService.confirmationAddFriend(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/cancel-request-add-friend/{id}")
    fun cancelRequestAddFriend(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, id.asUUID())
        return ResponseEntity(fiendService.cancelRequestAddFriend(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/reject-friend-request/{id}")
    fun rejectFriendRequest(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, id.asUUID())
        return ResponseEntity(fiendService.rejectFriendRequest(requestDTO), HttpStatus.OK)
    }

    @GetMapping("")
    fun getFriends(
        request: HttpServletRequest,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") sizePerPage: Int,
        @RequestParam(defaultValue = "") sortBy: String,
        @RequestParam(defaultValue = "") keyword: String
    ): ResponseEntity<ListResponseDTO<UserResponseDTO>> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val listRequestParams = ListRequestDTO(page, sizePerPage, sortBy, keyword)
        val requestDTO = RequestDTO(jwtBody, listRequestParams)
        return ResponseEntity(fiendService.getFriends(requestDTO), HttpStatus.OK)
    }

    @GetMapping("/channel/{id}/outside")
    fun getFriendsOutsideChannel(
        request: HttpServletRequest,
        @PathVariable id: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") sizePerPage: Int,
        @RequestParam(defaultValue = "") sortBy: String,
        @RequestParam(defaultValue = "") keyword: String,
    ): ResponseEntity<ListResponseDTO<UserResponseDTO>> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val listRequestParams = ListRequestDTO(page, sizePerPage, sortBy, keyword)
        val requestDTO = RequestDTO(jwtBody, listRequestParams, id.asUUID())
        return ResponseEntity(fiendService.getFriendsOutsideChannel(requestDTO), HttpStatus.OK)
    }

    @GetMapping("/request-add-friend")
    fun getListRequestAddFriend(
        request: HttpServletRequest,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") sizePerPage: Int,
        @RequestParam(defaultValue = "") sortBy: String,
        @RequestParam(defaultValue = "") keyword: String
    ): ResponseEntity<ListResponseDTO<UserResponseDTO>> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val listRequestParams = ListRequestDTO(page, sizePerPage, sortBy, keyword)
        val requestDTO = RequestDTO(jwtBody, listRequestParams)
        return ResponseEntity(fiendService.getListRequestAddFriend(requestDTO), HttpStatus.OK)
    }

    @GetMapping("/number-request-add-friend")
    fun getNumberRequestAddFriend(request: HttpServletRequest) : ResponseEntity<Long> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, Unit)
        return ResponseEntity(fiendService.getNumberRequestAddFriend(requestDTO), HttpStatus.OK)
    }
}