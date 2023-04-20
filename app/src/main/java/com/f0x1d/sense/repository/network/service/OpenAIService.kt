package com.f0x1d.sense.repository.network.service

import com.f0x1d.sense.model.network.request.GenerateImageRequestBody
import com.f0x1d.sense.model.network.request.GenerateMessagesRequestBody
import com.f0x1d.sense.model.network.response.GenerateImageResponse
import com.f0x1d.sense.model.network.response.GenerateMessagesResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIService {

    @POST("chat/completions")
    suspend fun generateMessages(@Body body: GenerateMessagesRequestBody): GenerateMessagesResponse

    @POST("images/generations")
    suspend fun generateImage(@Body body: GenerateImageRequestBody): GenerateImageResponse
}