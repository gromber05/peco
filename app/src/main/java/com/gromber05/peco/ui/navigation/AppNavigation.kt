package com.gromber05.peco.ui.navigation

sealed class AppNavigation(val route: String) {
    object MainScreen: AppNavigation(route = "home_screen")
    object LoginScreen: AppNavigation(route = "login_screen")
    object DetailScreen: AppNavigation(route = "detail/{animalId}")
    object RegisterScreen: AppNavigation(route = "register_screen")
    object EditProfileScreen: AppNavigation(route = "edit_profile")
    object EditPasswordScreen: AppNavigation(route = "change_password")

}