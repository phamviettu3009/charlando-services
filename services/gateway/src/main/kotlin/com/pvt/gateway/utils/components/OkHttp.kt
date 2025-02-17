package com.pvt.gateway.utils.components

import com.google.gson.Gson
import com.pvt.gateway.models.dtos.MessageDTO
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.net.URL

@Component
class OkHttp {
    val gson = Gson()
    val client = OkHttpClient()
    val mediaType = "application/json; charset=utf-8".toMediaType()

    fun <T, V> executePost(url: String, requestBody: T, responseClass: Class<V>): V? {
        val json = gson.toJson(requestBody)

        val request = Request.Builder()
            .url(url)
            .post(json.toRequestBody(mediaType))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println(response.body)

                val error = response.body?.string()?.let { json ->
                    gson.fromJson(json, MessageDTO::class.java)
                }
                throw ResponseStatusException(HttpStatus.valueOf(error?.status ?: 500), error?.message)
            }

            return response.body?.string()?.let { json ->
                return gson.fromJson(json, responseClass)
            }
        }
    }

    private fun extractParamsFromUrl(url: String): Map<String, String> {
        val urlObject = URL(url)
        val queryParams = urlObject.query ?: return emptyMap()

        val paramsArray = queryParams.split("&")
        val paramsMap = mutableMapOf<String, String>()

        for (param in paramsArray) {
            val pair = param.split("=")
            if (pair.size == 2) {
                val key = pair[0]
                val value = pair[1]
                paramsMap[key] = value
            }
        }

        return paramsMap
    }
}