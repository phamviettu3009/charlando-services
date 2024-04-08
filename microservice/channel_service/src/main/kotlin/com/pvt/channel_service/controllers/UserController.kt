package com.pvt.channel_service.controllers

import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.services.UserService
import com.pvt.channel_service.utils.extension.asUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/user")
class UserController {
    @Autowired
    private lateinit var userService: UserService

    @GetMapping("")
    fun getUsers(
        request: HttpServletRequest,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") sizePerPage: Int,
        @RequestParam(defaultValue = "") sortBy: String,
        @RequestParam(defaultValue = "") keyword: String
    ): ResponseEntity<ListResponseDTO<UserResponseDTO>> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val listRequestParams = ListRequestDTO(page, sizePerPage, sortBy, keyword)
        val requestDTO = RequestDTO(jwtBody, listRequestParams)
        return ResponseEntity(userService.getUsers(requestDTO), HttpStatus.OK)
    }

    @GetMapping("/owner")
    fun getUser(
        request: HttpServletRequest
    ): ResponseEntity<UserResponseDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, Unit)
        return ResponseEntity(userService.getUser(requestDTO), HttpStatus.OK)
    }

    @GetMapping("/channel/{id}/members")
    fun getUsersInChannel(
        request: HttpServletRequest,
        @PathVariable id: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") sizePerPage: Int,
        @RequestParam(defaultValue = "") sortBy: String,
        @RequestParam(defaultValue = "") keyword: String
    ): ResponseEntity<ListResponseDTO<MemberResponseDTO>> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val listRequestParams = ListRequestDTO(page, sizePerPage, sortBy, keyword)
        val requestDTO = RequestDTO(jwtBody, listRequestParams, id.asUUID())
        return ResponseEntity(userService.getUsersInChannel(requestDTO), HttpStatus.OK)
    }

    @GetMapping("/{id}/info")
    fun getUserInfo(
        request: HttpServletRequest,
        @PathVariable id: String
    ): ResponseEntity<UserInfoResponseDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, Unit, id.asUUID())
        return ResponseEntity(userService.getUserInfo(requestDTO), HttpStatus.OK)
    }

    @PutMapping("/self")
    fun updateUser(
        request: HttpServletRequest,
        @RequestBody userUpdateRequestDTO: UserUpdateRequestDTO
    ): ResponseEntity<UserResponseDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, userUpdateRequestDTO)
        return ResponseEntity(userService.updateUser(requestDTO), HttpStatus.OK)
    }
}
