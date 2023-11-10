package com.f0x1d.sense.model.network.request

import androidx.annotation.Keep
import com.f0x1d.sense.database.entity.ChatMessage
import com.google.gson.annotations.SerializedName

@Keep
data class GenerateMessagesRequestBody(
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("model") val model: String,
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("stream") val stream: Boolean = true
)
