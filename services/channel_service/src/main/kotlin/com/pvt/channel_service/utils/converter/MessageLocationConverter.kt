package com.pvt.channel_service.utils.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.pvt.channel_service.models.dtos.LocationDTO
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class MessageLocationConverter : AttributeConverter<LocationDTO?, String?> {
    private val objectMapper = ObjectMapper()

    override fun convertToDatabaseColumn(attribute: LocationDTO?): String? {
        return if (attribute == null) null else objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): LocationDTO? {
        return if (dbData == null) null else objectMapper.readValue(dbData, LocationDTO::class.java)
    }
}