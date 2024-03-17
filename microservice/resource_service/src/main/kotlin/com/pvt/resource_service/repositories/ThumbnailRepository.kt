package com.pvt.resource_service.repositories

import com.pvt.resource_service.models.entitys.ThumbnailEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ThumbnailRepository: JpaRepository<ThumbnailEntity, UUID> {

}