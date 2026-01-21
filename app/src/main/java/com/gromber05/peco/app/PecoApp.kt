package com.gromber05.peco.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.gromber05.peco.ui.AppViewModel
import com.gromber05.peco.ui.screens.chat.ConversationsScreen
import com.gromber05.peco.ui.screens.profile.ChangePasswordScreen
import com.gromber05.peco.ui.screens.profile.EditProfileScreen
import com.gromber05.peco.ui.screens.login.LoginViewModel
import com.gromber05.peco.ui.screens.register.RegisterScreen
import com.gromber05.peco.ui.screens.register.RegisterViewModel

@Composable
fun PecoApp(
    isDark: Boolean
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val appVm: AppViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()

    val logger by loginViewModel.uiState.collectAsState()
    val isLogged by appVm.isLoggedInOrNull.collectAsState(initial = false)

    val onToggleTheme = {appVm.toggleDarkMode()}

    if (isLogged == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando…")
        }
        return
    }

    LaunchedEffect(isLogged) {
        if (isLogged == true) {
            navController.navigate(AppNavigation.MainScreen.route) {
                popUpTo(AppNavigation.LoginScreen.route) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(AppNavigation.LoginScreen.route) {
                popUpTo(AppNavigation.MainScreen.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLogged == false) {
            AppNavigation.LoginScreen.route
        } else {
            AppNavigation.MainScreen.route
        }
    ) {
        composable(AppNavigation.LoginScreen.route) {
            LoginScreen(
                onToggleTheme = onToggleTheme,
                viewModel = loginViewModel,
                onNavigateToHome = {},
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
                viewModel = homeViewModel,
                isDarkMode = isDark,
                onToggleDarkMode = onToggleTheme,
                onLogout = { homeViewModel.logout() },
                onOpenEditProfile = { navController.navigate(AppNavigation.EditProfile.route) },
                onOpenChangePassword = { navController.navigate(AppNavigation.ChangePassword.route) },
                onOpenChats = {
                    navController.navigate(AppNavigation.Conversations.route)
                }
            )
        }

        composable(AppNavigation.Conversations.route) {
            ConversationsScreen(
                myUid = logger.user,
                isVolunteer = logger.isVolunteer,
                onOpenChat = { conversationId ->
                    navController.navigate(AppNavigation.Conversation.createRoute(conversationId))
                },
                onBack = { navController.popBackStack() }
            )
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

        composable(AppNavigation.EditProfile.route) { EditProfileScreen(onBack = { navController.popBackStack() }) }
        composable(AppNavigation.ChangePassword.route) { ChangePasswordScreen(onBack = { navController.popBackStack() }) }

    }

}