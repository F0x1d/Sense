package com.f0x1d.sense.model.network.response

import androidx.annotation.Keep
import com.f0x1d.sense.database.entity.ChatMessage
import com.google.gson.annotations.SerializedName

@Keep
data class GenerateMessagesResponse(
    @SerializedName("choices") val choices: List<MessageChoice>
)

@Keep
data class MessageChoice(
    @SerializedName("message") val message: ChatMessage? = null,
    @SerializedName("delta") val delta: Delta? = null,
    @SerializedName("index") val index: Int = -1
)

@Keep
data class Delta(
    @SerializedName("role") val role: String? = null,
    @SerializedName("content") val content: String? = null
)