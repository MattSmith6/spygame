package com.example.spygame

sealed class Screen(val route: String) {
    object LoginScreen: Screen("login_screen")
    object RegistrationScreen: Screen("registration_screen")
    object MenuScreen: Screen("menu_screen")
}
