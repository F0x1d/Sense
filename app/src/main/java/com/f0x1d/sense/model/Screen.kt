package com.f0x1d.sense.model

sealed class Screen(val route: String) {
    data object Chats: Screen("chats")
    data object Chat: Screen("chat")
    data object Pictures: Screen("pictures")
    data object Settings: Screen("settings")
}