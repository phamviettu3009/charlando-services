package com.pvt.auth_service.utils.components

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.springframework.stereotype.Component

@Component
class OkHttp {
    val gson = Gson()
    val client = OkHttpClient()
    val mediaType = "application/json; charset=utf-8".toMediaType()

    fun <T> executePost(url: String, requestBody: T): ResponseBody? {
        val json = gson.toJson(requestBody)

        val request = Request.Builder()
            .url(url)
            .post(json.toRequestBody(mediaType))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Unexpected code $response")
            }

            return response.body
        }
    }
}