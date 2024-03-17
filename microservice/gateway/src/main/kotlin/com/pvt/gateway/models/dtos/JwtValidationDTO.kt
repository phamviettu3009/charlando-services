package com.pvt.gateway.models.dtos

import com.google.gson.annotations.SerializedName

data class JwtValidationDTO(
    @SerializedName("token") var token: String?,
    @SerializedName("path") var path: String?,
    @SerializedName("method") var method: String
)

