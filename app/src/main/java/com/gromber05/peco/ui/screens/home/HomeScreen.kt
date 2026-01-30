package com.gromber05.peco.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.gromber05.peco.model.events.UiEvent
import com.gromber05.peco.model.user.UserRole
import com.gromber05.peco.ui.components.MyTopAppBar
import com.gromber05.peco.ui.screens.admin.AdminAddAnimalScreen
import com.gromber05.peco.ui.screens.admin.AdminScreen
import com.gromber05.peco.ui.screens.animals.AnimalsScreen
import com.gromber05.peco.ui.screens.conversation.ConversationsScreen

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onToggleDarkMode: () -> Unit,
    isDarkMode: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenChangePassword: () -> Unit,
    onOpenChat: (String) -> Unit,
    onAnimalClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectPage by rememberSaveable { mutableIntStateOf(0) }
    var adminPage by rememberSaveable { mutableIntStateOf(0) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        indicatorColor = MaterialTheme.colorScheme.secondaryContainer
    )

    BackHandler {
        when {
            selectPage == 3 && adminPage != 0 -> adminPage = 0
            selectPage != 0 -> selectPage = 0
            else -> onBack()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let { viewModel.sortByProximity(it.latitude, it.longitude) }
                }
            } catch (_: SecurityException) {}
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun getLocationAndOrganise() {
        try {
            val granted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let { viewModel.sortByProximity(it.latitude, it.longitude) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) getLocationAndOrganise()
        else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Error -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    val isAdmin = state.userRole == UserRole.ADMIN
    val isVolunteer = state.userRole == UserRole.VOLUNTEER

    Scaffold(
        topBar = { MyTopAppBar(name = state.username) },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            ) {
                NavigationBar(containerColor = Color.Transparent) {
                    NavigationBarItem(
                        selected = selectPage == 0,
                        onClick = { selectPage = 0 },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Menú de inicio") },
                        label = { Text("Inicio") },
                        colors = navItemColors
                    )

                    NavigationBarItem(
                        selected = selectPage == 1,
                        onClick = { selectPage = 1 },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Animales favoritos") },
                        label = { Text("Animales") },
                        colors = navItemColors
                    )


                    NavigationBarItem(
                        selected = selectPage == 2,
                        onClick = { selectPage = 2 },
                        icon = { Icon(Icons.Filled.Person3, contentDescription = "Menú tu Cuenta") },
                        label = { Text("Cuenta") },
                        colors = navItemColors
                    )

                    NavigationBarItem(
                        selected = selectPage == 4,
                        onClick = { selectPage = 4 },
                        icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chats") },
                        label = { Text("Chats") },
                        colors = navItemColors
                    )

                    if (isAdmin) {
                        NavigationBarItem(
                            selected = selectPage == 3,
                            onClick = { selectPage = 3 },
                            icon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Menú de Admin") },
                            label = { Text("Admin") },
                            colors = navItemColors
                        )
                    }

                }
            }
        }
    ) { innerPadding ->
        when (selectPage) {
            0 -> HomeView(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel
            )

            1 -> AnimalsScreen(
                modifier = Modifier.padding(innerPadding),
                onBack = onBack,
                onAnimalClick = onAnimalClick,
            )

            2 -> SettingsView(
                modifier = Modifier.padding(innerPadding),
                onToggleTheme = onToggleDarkMode,
                isDarkMode = isDarkMode,
                onLogout = onLogout,
                username = state.username,
                email = state.email,
                onOpenEditProfile = onOpenEditProfile,
                onOpenChangePassword = onOpenChangePassword,
                profilePhoto = state.photo,
                userRole = state.userRole,
            )

            3 -> {
                when (adminPage) {
                    0 -> AdminScreen(
                        modifier = Modifier.padding(innerPadding),
                        onBack = { selectPage = 0 },
                        onAddAnimal = { adminPage = 1 },
                        onManageAnimals = { adminPage = 2 }
                    )
                    1 -> AdminAddAnimalScreen(
                        modifier = Modifier.padding(innerPadding),
                        onBack = { adminPage = 0 },
                    )
                }
            }

            4 -> {
                ConversationsScreen(
                    modifier = Modifier.padding(innerPadding),
                    onBack = { selectPage = 0 },
                    onOpenChat = { conversationId ->
                        onOpenChat(conversationId)
                    }
                )
            }

        }

    }
}