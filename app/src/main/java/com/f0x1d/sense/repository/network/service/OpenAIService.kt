package com.f0x1d.sense.repository.network.service

import com.f0x1d.sense.model.network.request.GenerateImageRequestBody
import com.f0x1d.sense.model.network.request.GenerateMessagesRequestBody
import com.f0x1d.sense.model.network.response.GenerateImageResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OpenAIService {

    @POST("chat/completions")
    @Streaming
    fun generateMessagesStream(@Body body: GenerateMessagesRequestBody): Call<ResponseBody>

    @POST("images/generations")
    suspend fun generateImage(@Body body: GenerateImageRequestBody): GenerateImageResponse
}