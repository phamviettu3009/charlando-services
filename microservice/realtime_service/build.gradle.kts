import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.10"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
}

group = "com.pvt"
version = "0.0.6"

java {
	sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
	flatDir {
		dirs("lib")
	}
	mavenCentral()
}

dependencies {
	implementation("com.eatthepath:fast-uuid:0.2.0")
	implementation ("io.netty:netty-all:4.1.72.Final")
	implementation(files("lib/pushy.jar"))
	implementation("com.google.firebase:firebase-admin:9.2.0")
	implementation("com.corundumstudio.socketio:netty-socketio:2.0.6")
	implementation("org.springframework.boot:spring-boot-starter-amqp:2.7.8")
	implementation("com.google.code.gson:gson:2.10")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("io.jsonwebtoken:jjwt:0.9.1")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
