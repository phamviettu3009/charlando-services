package com.pvt.auth_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class JwtValidationDTO(
    @JsonProperty("token") var token: String?,
    @JsonProperty("path") var path: String?,
    @JsonProperty("method") var method: String
)
