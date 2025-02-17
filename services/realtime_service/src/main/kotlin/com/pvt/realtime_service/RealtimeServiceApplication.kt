package com.pvt.realtime_service

import com.pvt.realtime_service.services.SocketIOService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RealtimeServiceApplication: CommandLineRunner {
	@Autowired
	private lateinit var socketIOService: SocketIOService

	override fun run(vararg args: String?) {
		socketIOService.run()
	}
}

fun main(args: Array<String>) {
	runApplication<RealtimeServiceApplication>(*args)
}
