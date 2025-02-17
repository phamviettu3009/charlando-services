package com.pvt.channel_service.repositories

import com.pvt.channel_service.models.entitys.SearchKeywordsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SearchKeywordsRepository: JpaRepository<SearchKeywordsEntity, UUID> {
}