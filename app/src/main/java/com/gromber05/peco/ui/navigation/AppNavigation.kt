package com.gromber05.peco.ui.navigation

sealed class AppNavigation(val route: String) {
    object MainScreen: AppNavigation(route = "home_screen")
    object LoginScreen: AppNavigation(route = "login_screen")
    object MyAnimalsScreen: AppNavigation(route = "myanimals")
    object DetailScreen: AppNavigation(route = "detail/{animalId}") {
        fun createRoute(animalId: String) = "detail/$animalId"
    }
    object RegisterScreen: AppNavigation(route = "register_screen")
    object ForgotPassword : AppNavigation("forgot_password")
    object EditProfile: AppNavigation(route = "edit_profile")
    object ChangePassword: AppNavigation(route = "change_password")
    object Gate: AppNavigation(route = "gate")
    object AddAnimalScreen: AppNavigation(route = "addanimalscreen")
}