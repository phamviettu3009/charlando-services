package com.pvt.realtime_service.configs

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException

@Configuration
class FirebaseConfig {
    @Bean
    @Throws(IOException::class)
    fun firebaseAdmin(): FirebaseApp? {
        val refreshToken = ClassPathResource("firebase-service-account.json").inputStream
        val options: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(refreshToken))
            .setProjectId("chatflow-428d7")
            .build()
        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp?): FirebaseMessaging? {
        return FirebaseMessaging.getInstance(firebaseApp)
    }
}