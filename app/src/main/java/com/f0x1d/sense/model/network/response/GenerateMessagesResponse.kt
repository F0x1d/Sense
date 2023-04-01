package com.f0x1d.sense.model.network.response

import com.f0x1d.sense.database.entity.ChatMessage
import com.google.gson.annotations.SerializedName

data class GenerateMessagesResponse(
    @SerializedName("choices") val choices: List<MessageChoice>
)

data class MessageChoice(
    @SerializedName("message") val message: ChatMessage
)