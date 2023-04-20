package com.f0x1d.sense.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.f0x1d.sense.model.Screen
import com.f0x1d.sense.ui.screen.ChatScreen
import com.f0x1d.sense.ui.screen.ChatsScreen
import com.f0x1d.sense.ui.screen.PicturesScreen
import com.f0x1d.sense.ui.screen.SettingsScreen
import com.f0x1d.sense.ui.screen.SetupScreen
import com.f0x1d.sense.ui.theme.OpenAITheme
import com.f0x1d.sense.viewmodel.ChatViewModelAssistedFactory
import com.f0x1d.sense.viewmodel.MainViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            OpenAITheme {
                Surface(modifier = Modifier.imePadding()) {
                    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle(initialValue = "")

                    Crossfade(targetState = apiKey) {
                        if (it == null) {
                            SetupScreen()
                        } else {
                            val navController = rememberNavController()

                            NavHost(
                                navController = navController,
                                startDestination = Screen.Chats.route
                            ) {
                                composable(Screen.Chats.route) {
                                    ChatsScreen(navController = navController)
                                }

                                composable(
                                    route = "${Screen.Chat.route}/{id}",
                                    arguments = listOf(
                                        navArgument("id") { type = NavType.LongType }
                                    )
                                ) {
                                    ChatScreen(
                                        navController = navController,
                                        chatId = it.arguments?.getLong("id")!!
                                    )
                                }

                                composable(Screen.Pictures.route) {
                                    PicturesScreen(navController = navController)
                                }

                                composable(Screen.Settings.route) {
                                    SettingsScreen(navController = navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelFactoryProvider {
    fun chatViewModelFactory(): ChatViewModelAssistedFactory
}