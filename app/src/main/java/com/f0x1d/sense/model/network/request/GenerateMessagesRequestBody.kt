package com.f0x1d.sense.model.network.request

import com.f0x1d.sense.database.entity.ChatMessage
import com.google.gson.annotations.SerializedName

data class GenerateMessagesRequestBody(
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("model") val model: String
)
