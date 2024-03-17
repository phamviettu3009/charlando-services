package com.pvt.channel_service.controllers

import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.services.ChannelService
import com.pvt.channel_service.utils.extension.asUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/channel")
class ChannelController {
    @Autowired
    private lateinit var channelService: ChannelService

    @GetMapping("/{id}")
    fun getChannel(
        request: HttpServletRequest,
        @PathVariable id: String
    ): ResponseEntity<ResponseChannelDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, Unit, id = id.asUUID())
        return ResponseEntity(channelService.getChannel(requestDTO), HttpStatus.OK)
    }

    @GetMapping("")
    fun getChannels(
        request: HttpServletRequest,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") sizePerPage: Int,
        @RequestParam(defaultValue = "") sortBy: String,
        @RequestParam(defaultValue = "") keyword: String
    ): ResponseEntity<ListResponseDTO<ResponseChannelDTO>> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val listRequestParams = ListRequestDTO(page, sizePerPage, sortBy, keyword)
        val requestDTO = RequestDTO(jwtBody, listRequestParams)
        return ResponseEntity(channelService.getChannels(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/group")
    fun createGroupChannel(
        request: HttpServletRequest,
        @RequestBody groupChannelRequestDTO: GroupChannelRequestDTO
    ): ResponseEntity<ResponseChannelDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, groupChannelRequestDTO)
        return ResponseEntity(channelService.createGroupChannel(requestDTO), HttpStatus.OK)
    }

    @PutMapping("/group/{id}")
    fun updateGroupChannel(
        request: HttpServletRequest,
        @RequestBody groupChannelUpdateRequestDTO: GroupChannelUpdateRequestDTO,
        @PathVariable id: String
    ): ResponseEntity<ResponseChannelDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, groupChannelUpdateRequestDTO, id = id.asUUID())
        return ResponseEntity(channelService.updateGroupChannel(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/group/{id}/add-members")
    fun addMemberToGroup(
        request: HttpServletRequest,
        @RequestBody groupChannelMembersDTO: GroupChannelMembers,
        @PathVariable id: String
    ): ResponseEntity<ResponseChannelDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, groupChannelMembersDTO, id = id.asUUID())
        return ResponseEntity(channelService.addMembersToGroupChannel(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/group/{id}/remove-members")
    fun removeMemberGroup(
        request: HttpServletRequest,
        @RequestBody groupChannelMembersDTO: GroupChannelMembers,
        @PathVariable id: String
    ): ResponseEntity<ResponseChannelDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, groupChannelMembersDTO, id = id.asUUID())
        return ResponseEntity(channelService.removeMembersInGroupChannel(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/group/{id}/leave-group")
    fun leaveGroupChannel(
        request: HttpServletRequest,
        @PathVariable id: String
    ): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, Unit, id = id.asUUID())
        return ResponseEntity(channelService.leaveGroupChannel(requestDTO), HttpStatus.OK)
    }

    @GetMapping("/group/{id}/my-role")
    fun getRole(
        request: HttpServletRequest,
        @PathVariable id: String
    ): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, Unit, id = id.asUUID())
        return ResponseEntity(channelService.getRole(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/group/{id}/set-admin-role")
    fun setAdminRole(
        request: HttpServletRequest,
        @RequestBody groupChannelMembersDTO: GroupChannelMembers,
        @PathVariable id: String
    ): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, groupChannelMembersDTO, id = id.asUUID())
        return ResponseEntity(channelService.setAdminRole(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/group/{id}/revoke-admin-role")
    fun revokeAdminRole(
        request: HttpServletRequest,
        @RequestBody groupChannelMembersDTO: GroupChannelMembers,
        @PathVariable id: String
    ): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, groupChannelMembersDTO, id = id.asUUID())
        return ResponseEntity(channelService.revokeAdminRole(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/group/{id}/set-owner-role")
    fun setOwnerRole(
        request: HttpServletRequest,
        @RequestBody groupChannelMemberDTO: GroupChannelMember,
        @PathVariable id: String
    ): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, groupChannelMemberDTO, id = id.asUUID())
        return ResponseEntity(channelService.setOwnerRole(requestDTO), HttpStatus.OK)
    }
}