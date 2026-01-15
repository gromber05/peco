package com.gromber05.peco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.gromber05.peco.ui.theme.PerreraConnectTheme
import dagger.hilt.android.AndroidEntryPoint
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PerreraConnectTheme {
                PecoApp()
            }
        }
    }
}

@Composable
fun PecoApp() {
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
        )
        { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("animalId") ?: 0
            DetailScreen(animalId = id)
        }
    }
}


