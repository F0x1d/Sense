package com.f0x1d.sense.repository.network.interceptor

import com.f0x1d.sense.SenseApplication
import com.f0x1d.sense.store.datastore.SettingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.Interceptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeEndpointInterceptor @Inject constructor(
    private val settingsDataStore: SettingsDataStore
): Interceptor {
    override fun intercept(chain: Interceptor.Chain) = runBlocking {
        val endpoint = settingsDataStore.endpoint.first().trim().takeIf {
            it.isNotEmpty()
        } ?: SenseApplication.API_ENDPOINT

        val hostAndPathSegments = endpoint.split("/").filter {
            it.isNotEmpty()
        }
        val host = hostAndPathSegments.first()
        val pathSegments = hostAndPathSegments.drop(1)

        val originalUrl = chain.request().url

        val newUrl = HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .port(originalUrl.port)
            .apply {
                (pathSegments + originalUrl.pathSegments).forEach {
                    addPathSegment(it)
                }
            }
            .encodedQuery(originalUrl.encodedQuery)
            .build()

        val request = chain
            .request()
            .newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(request)
    }
}