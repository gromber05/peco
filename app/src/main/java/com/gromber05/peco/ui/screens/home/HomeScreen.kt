package com.gromber05.peco.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.gromber05.peco.model.events.UiEvent
import com.gromber05.peco.ui.components.AnimalCard
import com.gromber05.peco.ui.components.MyTopAppBar
import com.gromber05.peco.ui.components.TinderSwipeDeck
import com.gromber05.peco.ui.screens.admin.AdminAddAnimalScreen
import com.gromber05.peco.ui.screens.admin.AdminScreen

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onToggleDarkMode: () -> Unit,
    isDarkMode: Boolean,
    onLogout: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenChangePassword: () -> Unit,
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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        viewModel.sortByProximity(it.latitude, it.longitude)
                    }
                }
            } catch (_: SecurityException) {}
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun obtenerUbicacionYOrdenar() {
        try {
            val granted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        viewModel.sortByProximity(it.latitude, it.longitude)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) obtenerUbicacionYOrdenar()
        else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                UiEvent.LoggedOut -> onLogout()
                is UiEvent.Error -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

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
                        icon = { Icon(Icons.Filled.Person3, contentDescription = "Menú tu Cuenta") },
                        label = { Text("Cuenta") },
                        colors = navItemColors
                    )

                    if (state.isAdmin) {
                        NavigationBarItem(
                            selected = selectPage == 2,
                            onClick = { selectPage = 2 },
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
            0 -> HomeView(modifier = Modifier.padding(innerPadding))
            1 -> SettingsView(
                modifier = Modifier.padding(innerPadding),
                onToggleTheme = onToggleDarkMode,
                isDarkMode = isDarkMode,
                onLogout = onLogout,
                username = state.username,
                email = state.email,
                isAdmin = state.isAdmin,
                onOpenEditProfile = onOpenEditProfile,
                onOpenChangePassword = onOpenChangePassword,
                profilePhoto = state.photo
            )
            2 -> {
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
        }
    }
}

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TinderSwipeDeck(
                items = state.deck,
                modifier = Modifier.fillMaxSize(),
                keyOf = { it.id },
                cardContent = { animal ->
                    AnimalCard(animal = animal) {
                    }
                },
                onLike = { animal ->
                    viewModel.onLike(animal)
                },
                onDislike = { animal ->
                    viewModel.onDislike(animal)
                },
                onEmpty = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No hay más animales por hoy 🐾")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.resetSwipes() }) {
                            Text("Volver a empezar")
                        }
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(
                onClick = { viewModel.dislikeCurrent() },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Descartar")
            }

            FilledIconButton(
                onClick = { viewModel.likeCurrent() },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = "Me gusta")
            }
        }
    }
}