package com.gromber05.peco.ui.navigation

sealed class AppNavigation(val route: String) {
    object MainScreen: AppNavigation(route = "home_screen")
    object LoginScreen: AppNavigation(route = "login_screen")
    object AnimalScreen: AppNavigation(route = "animals")
    object DetailScreen: AppNavigation(route = "detail/{animalId}") {
        fun createRoute(animalId: Int) = "detail/$animalId"
    }
    object RegisterScreen: AppNavigation(route = "register_screen")
    object EditProfile: AppNavigation(route = "edit_profile")
    object ChangePassword: AppNavigation(route = "change_password")
    object Conversations : AppNavigation("conversations")

    object Chat : AppNavigation("chat/{conversationId}") {
        fun createRoute(conversationId: String) = "chat/$conversationId"
    }

}