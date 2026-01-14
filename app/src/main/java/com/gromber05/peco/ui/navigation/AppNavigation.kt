package com.gromber05.peco.ui.navigation

sealed class AppNavigation(val route: String) {
    object MainScreen: AppNavigation(route = "home_screen")
    object LoginScreen: AppNavigation(route = "login_screen")
    object DetailScreen: AppNavigation(route = "detail/{animalId}")
    object AdminScreen: AppNavigation(route = "admin_home_screen")
    object RegisterScreen: AppNavigation(route = "register_screen")
}