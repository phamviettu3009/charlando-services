package com.pvt.channel_service.controllers

import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.services.MessageReactionService
import com.pvt.channel_service.services.MessageService
import com.pvt.channel_service.utils.extension.asUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/messages")
class MessageController {
    @Autowired
    private lateinit var messageService: MessageService

    @Autowired
    private lateinit var messageReactionService: MessageReactionService

    @PostMapping("/channels/{id}")
    fun createMessage(
        request: HttpServletRequest,
        @PathVariable id: String,
        @RequestBody messageRequest: MessageRequestDTO
    ): ResponseEntity<ResponseMessageDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, messageRequest, id = id.asUUID())
        return ResponseEntity(messageService.createMessage(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/{id}/reaction")
    fun createMessageReaction(
        request: HttpServletRequest,
        @PathVariable id: String,
        @RequestBody icon: MessageReactionRequestDTO
    ): ResponseEntity<ResponseMessageDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, icon, id = id.asUUID())
        return ResponseEntity(messageReactionService.createMessageReaction(requestDTO), HttpStatus.OK)
    }

    @GetMapping("/channels/{id}")
    fun getMessages(
        request: HttpServletRequest,
        @PathVariable id: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") sizePerPage: Int,
        @RequestParam(defaultValue = "") sortBy: String,
        @RequestParam(defaultValue = "") keyword: String
    ): ResponseEntity<ListResponseDTO<ResponseMessageDTO>> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val listRequestParams = ListRequestDTO(page, sizePerPage, sortBy, keyword)
        val requestDTO = RequestDTO(jwtBody, listRequestParams, id = id.asUUID())
        return ResponseEntity(messageService.getMessages(requestDTO), HttpStatus.OK)
    }

    @DeleteMapping("/{id}/for-all")
    fun deleteMessageForAll(
        request: HttpServletRequest,
        @PathVariable id: String,
    ) : ResponseEntity<ResponseMessageDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, "delete_for_all", id = id.asUUID())
        return ResponseEntity(messageService.deleteMessage(requestDTO), HttpStatus.OK)
    }

    @DeleteMapping("/{id}/for-owner")
    fun deleteMessageForOwner(
        request: HttpServletRequest,
        @PathVariable id: String,
    ) : ResponseEntity<ResponseMessageDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, "delete_for_owner", id = id.asUUID())
        return ResponseEntity(messageService.deleteMessage(requestDTO), HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateMessage(
        request: HttpServletRequest,
        @PathVariable id: String,
        @RequestBody messageUpdateRequest: MessageUpdateRequestDTO
    ): ResponseEntity<ResponseMessageDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, messageUpdateRequest, id = id.asUUID())
        return ResponseEntity(messageService.updateMessage(requestDTO), HttpStatus.OK)
    }

    @PostMapping("/{id}/read-message")
    fun readMessage(
        request: HttpServletRequest,
        @PathVariable id: String,
    ): ResponseEntity<MutableList<AvatarDTO>> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, Unit, id = id.asUUID())
        return ResponseEntity(messageService.readMessage(requestDTO), HttpStatus.OK)
    }
}