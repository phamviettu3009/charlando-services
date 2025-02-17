package com.pvt.auth_service.constants

import com.pvt.auth_service.models.dtos.RecordLevelAccessShouldBeChecked

object Common {
    const val SALT: String = "pvt@#$1"

    val LIST_RECORD_LEVEL_ACCESS = listOf(
        RecordLevelAccessShouldBeChecked(
            path = "/resource/get/[a-fA-F0-9\\\\-]+",
            method = "GET"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/channel/[a-fA-F0-9\\\\-]+",
            method = "GET"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/message/channel/[a-fA-F0-9\\\\-]+",
            method = "GET"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/message/channel/[a-fA-F0-9\\\\-]+",
            method = "POST"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/message/channel/[a-fA-F0-9\\\\-]+",
            method = "POST"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/channel/group/[a-fA-F0-9\\\\-]+",
            method = "PUT"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/channel/group/[a-fA-F0-9\\\\-]+/add-members",
            method = "POST"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/channel/group/[a-fA-F0-9\\\\-]+/remove-members",
            method = "POST"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/message/[a-fA-F0-9\\\\-]+",
            method = "PUT"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/message/[a-fA-F0-9\\\\-]+/reaction",
            method = "POST"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/message/[a-fA-F0-9\\\\-]+/for-all",
            method = "DELETE"
        ),
        RecordLevelAccessShouldBeChecked(
            path = "/message/[a-fA-F0-9\\\\-]+/for-owner",
            method = "DELETE"
        ),
    )
}