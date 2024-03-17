package com.pvt.channel_service.services

import com.pvt.channel_service.models.entitys.MemberEntity
import com.pvt.channel_service.repositories.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class MemberServiceImpl: MemberService {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    override fun findByChannelIDAndUserID(channelID: UUID, userID: UUID): MemberEntity {
        return memberRepository.findByChannelIDAndUserID(channelID, userID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
    }

    override fun findAllByChannelID(channelID: UUID): List<MemberEntity> {
        return memberRepository.findAllByChannelID(channelID)
    }
}