package com.gromber05.peco.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gromber05.peco.ui.navigation.AppNavigation
import com.gromber05.peco.ui.screens.detail.DetailScreen
import com.gromber05.peco.ui.screens.home.HomeScreen
import com.gromber05.peco.ui.screens.home.HomeViewModel
import com.gromber05.peco.ui.screens.login.LoginScreen
import com.gromber05.peco.ui.screens.login.LoginViewModel
import com.gromber05.peco.ui.screens.register.RegisterScreen
import com.gromber05.peco.ui.screens.register.RegisterViewModel

@Composable
fun PecoApp(
    onToggleTheme: () -> Unit,
    isDark: Boolean
) {
    val navController = rememberNavController()

    val loginViewModel: LoginViewModel = hiltViewModel()
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()

    val logger by loginViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (!logger.isLoggedIn) {
            AppNavigation.LoginScreen.route
        } else {
            if (logger.isAdmin) {
                AppNavigation.AdminScreen.route
            } else {
                AppNavigation.MainScreen.route
            }
        }
    ) {
        composable(AppNavigation.LoginScreen.route) {
            LoginScreen(
                onToggleTheme = onToggleTheme,
                isDarkMode = isDark,
                viewModel = loginViewModel,
                onNavigateToHome = {
                    navController.navigate(AppNavigation.MainScreen.route) {
                        popUpTo(AppNavigation.LoginScreen.route) { inclusive = true }
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate(AppNavigation.AdminScreen.route) {
                        popUpTo(AppNavigation.LoginScreen.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppNavigation.RegisterScreen.route)
                }
            )
        }

        composable(AppNavigation.RegisterScreen.route) {
            RegisterScreen(
                onToggleTheme = onToggleTheme,
                isDarkMode = isDark,
                viewModel = registerViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppNavigation.MainScreen.route) {
            HomeScreen(
                onToggleTheme = onToggleTheme,
                isDarkMode = isDark,
                viewModel = homeViewModel,
                email = logger.email
            )
        }
        composable(AppNavigation.AdminScreen.route) {
            // AdminScreen()
        }

        composable(
            route = AppNavigation.DetailScreen.route,
            arguments = listOf(navArgument("animalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("animalId") ?: 0
            DetailScreen(
                isDarkMode = isDark,
                animalId = id
            )
        }
    }
}