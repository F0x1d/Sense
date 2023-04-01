package com.f0x1d.sense.repository.network

import com.f0x1d.sense.database.entity.ChatMessage
import com.f0x1d.sense.model.network.request.GenerateMessagesRequestBody
import com.f0x1d.sense.repository.base.BaseRepository
import com.f0x1d.sense.repository.network.service.OpenAIService
import com.f0x1d.sense.store.datastore.SettingsDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIRepository @Inject constructor(
    private val openAIService: OpenAIService,
    private val settingsDataStore: SettingsDataStore
): BaseRepository() {

    suspend fun generateMessages(messages: List<ChatMessage>) = openAIService.generateMessages(
        GenerateMessagesRequestBody(
            messages,
            settingsDataStore.model.first()
        )
    ).choices.map { it.message }
}