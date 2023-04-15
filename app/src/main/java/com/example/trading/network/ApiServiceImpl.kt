package com.example.trading.network

import com.example.trading.vo.ResponseVO
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlin.text.get

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {

    override suspend fun getProducts(): ResponseVO? {
        return try {
            client.get { url(Endpoints.BASE_URL) }
        } catch (ex: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${ex.response.status.description}")
            null
        } catch (ex: ClientRequestException) {
            // 4xx - responses
            println("Error: ${ex.response.status.description}")
            null
        } catch (ex: ServerResponseException) {
            // 5xx - response
            println("Error: ${ex.response.status.description}")
            null
        }
    }
}