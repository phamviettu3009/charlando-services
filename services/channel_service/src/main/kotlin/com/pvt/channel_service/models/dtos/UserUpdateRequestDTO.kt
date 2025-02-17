package com.pvt.channel_service.models.dtos

data class UserUpdateRequestDTO(
    var fullName: String?,
    var avatar: String?,
    var coverPhoto: String?,
    var gender: String?,
    var dob: String?,
    var phone: String?,
    var email: String?,
    var countryCode: String?,
    var languageCode: String?,
)
