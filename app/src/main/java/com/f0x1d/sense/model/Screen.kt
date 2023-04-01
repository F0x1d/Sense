package com.f0x1d.sense.model

sealed class Screen(val route: String) {
    object Setup: Screen("setup")
    object Chats: Screen("chats")
    object Chat: Screen("chat")
    object Settings: Screen("settings")
}