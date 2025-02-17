package com.pvt.auth_service.utils

class RabbitQMAutoGenerate(private val name: String, private val msName: String) {
    fun queue() = "${msName}.${name}_queue"
    fun callbackQueue() = "${msName}.${name}_callback_queue"
    fun route() = "${msName}.${name}_route"
    fun callbackRoute() = "${msName}.${name}_callback_route"
}