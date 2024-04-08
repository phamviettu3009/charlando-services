package com.pvt.channel_service.constants

import com.pvt.channel_service.utils.RabbitQMAutoGenerate

object RabbitMQ {
    val MSCMN_CREATE_RECORD_LEVEL_ACCESS = RabbitQMAutoGenerate("crla", "mscmn")
    val MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS = RabbitQMAutoGenerate("rrlafm", "mscmn")
    val MSCMN_GET_USER_BY_ID = RabbitQMAutoGenerate("gubi", "mscmn")
    val MSCMN_GET_RESOURCE_BY_ID_AND_USERID = RabbitQMAutoGenerate("grbiau", "mscmn")
    val MSCMN_SEND_REALTIME_MESSAGE = RabbitQMAutoGenerate("srm", "mscmn")

    val MSC_CREATE_RECORD_USER = RabbitQMAutoGenerate("cru", "msc")
    val MSC_UPDATE_RECORD_USER = RabbitQMAutoGenerate("uru", "msc")
    val MSC_UPDATE_ONLINE_STATUS_RECORD_USER = RabbitQMAutoGenerate("uosru", "msc")

    object Listener {
        const val MSCMN_CREATE_RECORD_LEVEL_ACCESS = "mscmn.crla_queue"
        const val MSCMN_CALLBACK_CREATE_RECORD_LEVEL_ACCESS = "mscmn.crla_callback_queue"
        const val MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS = "mscmn.rrlafm_queue"
        const val MSCMN_GET_USER_BY_ID = "mscmn.gubi_queue"
        const val MSCMN_CALLBACK_RESOURCE_BY_ID_AND_USERID = "mscmn.grbiau_callback_queue"
        const val MSC_CREATE_RECORD_USER = "msc.cru_queue"
        const val MSC_UPDATE_RECORD_USER = "msc.uru_queue"
        const val MSC_UPDATE_ONLINE_STATUS_RECORD_USER = "msc.uosru_queue"
    }

    object Exchange {
        const val QUEUE_EXCHANGE = "queue_exchange"
    }
}