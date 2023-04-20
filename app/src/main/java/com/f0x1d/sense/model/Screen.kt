package com.f0x1d.sense.model

sealed class Screen(val route: String) {
    object Chats: Screen("chats")
    object Chat: Screen("chat")
    object Pictures: Screen("pictures")
    object Settings: Screen("settings")
}