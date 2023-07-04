package com.f0x1d.sense

import android.app.Application
import com.f0x1d.sense.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SenseApplication: Application() {

    companion object {
        val applicationScope = MainScope()
    }

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch(Dispatchers.IO) {
            database.messagesDao().apply {
                markAllAsNotGenerating()
                deleteEmptyMessages()
            } // maybe some geniuses will remove app from recents
        }
    }
}