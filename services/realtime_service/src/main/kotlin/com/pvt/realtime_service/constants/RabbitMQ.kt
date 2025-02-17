package com.pvt.realtime_service.constants

import com.pvt.realtime_service.utils.RabbitQMAutoGenerate

object RabbitMQ {
    val MSCMN_VALIDATION_JWT = RabbitQMAutoGenerate("vj", "mscmn")
    val MSCMN_SEND_REALTIME_MESSAGE = RabbitQMAutoGenerate("srm", "mscmn")
    val MSCMN_SEND_NOTIFICATION_MESSAGE = RabbitQMAutoGenerate("snm", "mscmn")
    val MSCMN_GET_DEVICE = RabbitQMAutoGenerate("gd", "mscmn")
    val MSCMN_WAKE_UP_DEVICES = RabbitQMAutoGenerate("wud", "mscmn")
    val MSC_UPDATE_ONLINE_STATUS_RECORD_USER = RabbitQMAutoGenerate("uosru", "msc")
    val MSC_TYPING = RabbitQMAutoGenerate("typing", "msc")

    object Listener {
        const val MSCMN_SEND_REALTIME_MESSAGE = "mscmn.srm_queue"
        const val MSCMN_SEND_NOTIFICATION_MESSAGE = "mscmn.snm_queue"
        const val MSCMN_WAKE_UP_DEVICES = "mscmn.wud_queue"
    }

    object Exchange {
        const val QUEUE_EXCHANGE = "queue_exchange"
    }
}