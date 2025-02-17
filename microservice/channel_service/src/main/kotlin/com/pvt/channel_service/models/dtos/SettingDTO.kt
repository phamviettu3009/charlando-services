package com.pvt.channel_service.models.dtos

data class SettingDTO(
    var publicEmail: Boolean,
    var publicGender: Boolean,
    var publicPhone: Boolean,
    var publicDob: Boolean
)
