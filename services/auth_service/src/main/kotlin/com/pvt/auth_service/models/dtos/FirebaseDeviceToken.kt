package com.pvt.auth_service.models.dtos

data class FirebaseDeviceToken(
    var deviceID: String,
    var firebaseToken: String
)
