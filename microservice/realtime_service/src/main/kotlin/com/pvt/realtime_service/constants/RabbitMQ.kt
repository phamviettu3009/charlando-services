package com.pvt.realtime_service.constants

import com.pvt.realtime_service.utils.RabbitQMAutoGenerate

object RabbitMQ {
    val MSCMN_VALIDATION_JWT = RabbitQMAutoGenerate("vj", "mscmn")
    val MSCMN_SEND_REALTIME_MESSAGE = RabbitQMAutoGenerate("srm", "mscmn")

    object Listener {
        const val MSCMN_SEND_REALTIME_MESSAGE = "mscmn.srm_queue"
    }

    object Exchange {
        const val QUEUE_EXCHANGE = "queue_exchange"
    }
}