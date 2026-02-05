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
import com.gromber05.peco.ui.AppViewModel
import com.gromber05.peco.ui.navigation.AppNavigation
import com.gromber05.peco.ui.screens.admin.AdminAddAnimalScreen
import com.gromber05.peco.ui.screens.animals.AnimalsScreen
import com.gromber05.peco.ui.screens.detail.DetailScreen
import com.gromber05.peco.ui.screens.forgotpassword.ForgotPasswordScreen
import com.gromber05.peco.ui.screens.gate.AuthGate
import com.gromber05.peco.ui.screens.home.HomeScreen
import com.gromber05.peco.ui.screens.home.HomeViewModel
import com.gromber05.peco.ui.screens.login.LoginScreen
import com.gromber05.peco.ui.screens.profile.ChangePasswordScreen
import com.gromber05.peco.ui.screens.profile.EditProfileScreen
import com.gromber05.peco.ui.screens.register.RegisterScreen

/**
 * Composable raíz de la aplicación PECO.
 *
 * Se encarga de:
 * - Inicializar el [NavController] principal.
 * - Gestionar la navegación entre pantallas mediante [NavHost].
 * - Conectar con los ViewModels globales usando Hilt.
 * - Controlar el estado de autenticación y el tema (modo claro / oscuro).
 *
 * Define el grafo de navegación de la aplicación, incluyendo:
 * - Pantallas de autenticación (login, registro, recuperación de contraseña).
 * - Pantalla principal y navegación interna.
 * - Pantallas de detalle, perfil y gestión de animales.
 *
 * @param isDark Indica si el tema actual de la aplicación es oscuro.
 */
@Composable
fun PecoApp(
    isDark: Boolean
) {
    val navController = rememberNavController()

    val appVm: AppViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()

    val onToggleTheme = {appVm.toggleDarkMode()}
    val isLogged: Boolean? by appVm.isLoggedInOrNull.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AppNavigation.Gate.route
    ) {
        composable(AppNavigation.Gate.route) {
            AuthGate(
                onGoHome = {
                    navController.navigate(AppNavigation.MainScreen.route) {
                        popUpTo(AppNavigation.Gate.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoLogin = {
                    navController.navigate(AppNavigation.LoginScreen.route) {
                        popUpTo(AppNavigation.Gate.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppNavigation.LoginScreen.route) {
            LoginScreen(
                onToggleTheme = onToggleTheme,
                onNavigateToHome = {
                    navController.navigate(AppNavigation.MainScreen.route) {
                        popUpTo(AppNavigation.LoginScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppNavigation.RegisterScreen.route)
                },
                onNavigateToResetPassword = {
                    navController.navigate(AppNavigation.ForgotPassword.route)
                }
            )
        }

        composable(AppNavigation.RegisterScreen.route) {
            RegisterScreen(
                onToggleTheme = onToggleTheme,
                isDarkMode = isDark,
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
                isDarkMode = isDark,
                onToggleDarkMode = onToggleTheme,
                onLogout = {
                    navController.navigate(AppNavigation.LoginScreen.route) { popUpTo(0) { inclusive = true}
                        launchSingleTop = true }
                    homeViewModel.logout() },
                onBack = { navController.popBackStack() },
                onOpenEditProfile = { navController.navigate(AppNavigation.EditProfile.route) },
                onOpenChangePassword = { navController.navigate(AppNavigation.ChangePassword.route) },
                onAnimalClick = { animalId ->
                    navController.navigate(AppNavigation.DetailScreen.createRoute(animalId))
                },
                onMyAnimals = { navController.navigate(AppNavigation.MyAnimalsScreen.route) }
            )
        }

        composable(
            route = AppNavigation.MyAnimalsScreen.route
        ) {
            AnimalsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAnimalClick = { animalId ->
                    navController.navigate(AppNavigation.DetailScreen.createRoute(animalId))
                },
                onAddAnimal = {
                    navController.navigate(AppNavigation.AddAnimalScreen.route)
                },
                ownAnimals = true
            )
        }

        composable ( route = AppNavigation.AddAnimalScreen.route ) {
            AdminAddAnimalScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = AppNavigation.DetailScreen.route,
            arguments = listOf(navArgument("animalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("animalId").toString()
            DetailScreen(
                animalId = id,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = AppNavigation.EditProfile.route) {
            EditProfileScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = AppNavigation.ChangePassword.route) {
            ChangePasswordScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppNavigation.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }
}