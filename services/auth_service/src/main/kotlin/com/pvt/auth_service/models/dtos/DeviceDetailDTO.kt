package com.pvt.auth_service.models.dtos

import com.fasterxml.jackson.databind.ObjectMapper
import javax.persistence.AttributeConverter
import javax.persistence.Converter


data class DeviceDetailDTO(
    var ram: String? = "",
    var rom: String? = ""
)

@Converter
class DeviceDetailConverter : AttributeConverter<DeviceDetailDTO?, String?> {
    private val objectMapper = ObjectMapper()

    override fun convertToDatabaseColumn(attribute: DeviceDetailDTO?): String? {
        return if (attribute == null) null else objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): DeviceDetailDTO? {
        return if (dbData == null) null else objectMapper.readValue(dbData, DeviceDetailDTO::class.java)
    }
}