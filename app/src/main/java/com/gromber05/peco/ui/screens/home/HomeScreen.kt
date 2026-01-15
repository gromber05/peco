package com.gromber05.peco.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.gromber05.peco.ui.components.AnimalCard
import com.gromber05.peco.ui.components.MyTopAppBar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    isDarkMode: Boolean,
    onNavigateToAdmin: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectPage by rememberSaveable() { mutableIntStateOf(0) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.sortByProximity(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) {

            }
        }
    }

    val obtenerUbicacionYOrdenar = {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.sortByProximity(location.latitude, location.longitude)
                    }
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    LaunchedEffect(Unit) {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            obtenerUbicacionYOrdenar()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        topBar = {
            MyTopAppBar(name = state.username)
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 8.dp,
                shape = RoundedCornerShape(
                    topStart = 30.dp,
                    topEnd = 30.dp
                )

            ) {
                NavigationBar(containerColor = Color.Transparent){
                    NavigationBarItem(
                        selected = selectPage == 0,
                        onClick = {
                            selectPage = 0
                        },
                        icon = {
                            Icon(Icons.Filled.Home, contentDescription = "Menú de inicio")
                        },
                        label = { Text("Inicio") }
                    )

                    NavigationBarItem(
                        selected = selectPage == 1,
                        onClick = {
                            selectPage = 1
                        },
                        icon = {
                            Icon(Icons.Filled.Settings, contentDescription = "Ajustes")
                        },
                        label = { Text("Ajustes") }
                    )

                    NavigationBarItem(
                        selected = selectPage == 2,
                        onClick = {
                            selectPage = 2
                        },
                        icon = {
                            Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Admin")
                        },
                        label = { Text("Admin") }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectPage) {
            0 -> {
                SettingsView(Modifier.padding(innerPadding))
            }
            1 -> @Composable {
                HomeView(Modifier.padding(innerPadding))
            }
            2 -> {
                onNavigateToAdmin()
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
        modifier = modifier
    ) {
        LazyColumn {
            items(state.animalList) { animal ->
                AnimalCard(animal = animal){

                }
            }
        }
    }
}

@Composable
fun SettingsView(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
    ) {

    }
}

@Preview
@Composable
fun Preview_HomeScreen() {
    HomeScreen(
        viewModel = TODO(),
        isDarkMode = TODO(),
        onNavigateToAdmin = TODO()
    )
}