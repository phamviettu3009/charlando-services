package com.pvt.channel_service.constants

object Message {
    object RecordStatus {
        const val DELETE_FOR_ALL = "1"
        const val DELETE_FOR_OWNER = "2"
    }
    object Type {
        const val MESSAGE = 1
        const val ATTACHMENT = 2
        const val ICON_MESSAGE = 3
    }
}