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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.gromber05.peco.ui.components.AnimalCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    email: String = "",
    onToggleTheme: () -> Unit,
    isDarkMode: Boolean,
) {
    val state by viewModel.uiState.collectAsState()
    var selectPage by rememberSaveable() { mutableStateOf(0) }

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
        topBar = { MyTopAppBar(name = state.username) }
    ) { innerPadding ->
        when (selectPage) {
            0 -> {
                SettingsView(Modifier.padding(innerPadding))
            }
            1 -> @Composable {
                HomeView(Modifier.padding(innerPadding))
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

@Composable
fun MyTopAppBar(name: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(
            bottomStart = 30.dp,
            bottomEnd = 30.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¡Hola, ${name}!",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp
            )
        }
    }
}

@Preview
@Composable
fun Preview_MyTopAppBar() {
    MyTopAppBar(
        name = "Gonzalo"
    )
}

@Preview
@Composable
fun Preview_HomeScreen() {
    HomeScreen(
        viewModel = TODO(),
        email = TODO(),
        onToggleTheme = TODO(),
        isDarkMode = TODO()
    )
}