package com.pvt.resource_service.constants

import com.pvt.resource_service.utils.RabbitQMAutoGenerate

object RabbitMQ {
    val MSCMN_CREATE_RECORD_LEVEL_ACCESS = RabbitQMAutoGenerate("crla", "mscmn")
    val MSCMN_GET_RESOURCE_BY_ID_AND_USERID = RabbitQMAutoGenerate("grbiau", "mscmn")

    object Listener {
        const val MSCMN_CREATE_RECORD_LEVEL_ACCESS = "mscmn.crla_queue"
        const val MSCMN_GET_RESOURCE_BY_ID_AND_USERID = "mscmn.grbiau_queue"
        const val MSCMN_CALLBACK_GET_RESOURCE_BY_ID_AND_USERID = "mscmn.grbiau_callback_queue"
    }

    object Exchange {
        const val QUEUE_EXCHANGE = "queue_exchange"
    }
}