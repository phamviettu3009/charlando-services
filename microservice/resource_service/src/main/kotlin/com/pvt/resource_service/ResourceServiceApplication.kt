package com.pvt.resource_service

import com.pvt.resource_service.services.UploadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ResourceServiceApplication: CommandLineRunner {
	@Autowired
	lateinit var uploadService: UploadService

	override fun run(vararg args: String?) {
		uploadService.init()
			.onFailure { throw RuntimeException("System cannot start up because no uploads folder is set up") }
	}
}

fun main(args: Array<String>) {
	runApplication<ResourceServiceApplication>(*args)
}
