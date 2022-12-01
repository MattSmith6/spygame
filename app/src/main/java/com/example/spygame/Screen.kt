package com.example.spygame
//This class contains screens that can be navigated to and from
sealed class Screen(val route: String) {
    object LoginScreen: Screen("login_screen")
    object RegisterScreen: Screen("registration_screen")
    object MenuScreen: Screen("menu_screen")
}
