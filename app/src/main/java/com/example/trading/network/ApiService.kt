package com.example.trading.network

import com.example.trading.vo.ResponseVO
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString

interface ApiService {

    suspend fun getProducts(): ResponseVO?

    companion object {

        fun create(): ApiService {
            return ApiServiceImpl(
                client = HttpClient(Android) {
                    // Logging
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    // JSON
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(json)
                    }
                    // Timeout
                    install(HttpTimeout) {
                        requestTimeoutMillis = 15000L
                        connectTimeoutMillis = 15000L
                        socketTimeoutMillis = 15000L
                    }
                    defaultRequest {
                        if (method != HttpMethod.Get) contentType(ContentType.Application.Json)
                        accept(ContentType.Application.Json)
                    }
                }
            )
        }

        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        inline fun <reified T: Any> deserializeData(request: String): T? {
            try {
                return json.decodeFromString<T>(request)
            } catch(e: Exception) {
                println(e)
            }
            return null
        }
    }
}