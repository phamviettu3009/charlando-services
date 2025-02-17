package com.pvt.channel_service.controllers

import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.services.UserService
import com.pvt.channel_service.utils.extension.asUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/users")
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

    @GetMapping("/{id}")
    fun getUser(
        request: HttpServletRequest,
        @PathVariable id: String
    ): ResponseEntity<Any> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO

        return if (id == "self") {
            val requestDTO = RequestDTO(jwtBody, Unit)
            ResponseEntity(userService.getUser(requestDTO), HttpStatus.OK)
        } else {
            val requestDTO = RequestDTO(jwtBody, UUID.randomUUID(), id.asUUID())
            ResponseEntity(userService.getUser(requestDTO), HttpStatus.OK)
        }
    }

    @PutMapping("/self")
    fun updateUser(
        request: HttpServletRequest,
        @RequestBody userUpdateRequestDTO: UserUpdateRequestDTO
    ): ResponseEntity<ExpandUserResponseDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, userUpdateRequestDTO)
        return ResponseEntity(userService.updateUser(requestDTO), HttpStatus.OK)
    }

    @PutMapping("/self/setting")
    fun  updateSetting(
        request: HttpServletRequest,
        @RequestBody settingDTO: SettingDTO
    ): ResponseEntity<SettingDTO> {
        val jwtBody = request.getAttribute("jwtBody") as JWTBodyDTO
        val requestDTO = RequestDTO(jwtBody, settingDTO)
        return ResponseEntity(userService.updateSetting(requestDTO), HttpStatus.OK)
    }
}
