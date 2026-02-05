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

/**
 * Pantalla principal (Home) que actúa como "contenedor" de navegación interna mediante bottom bar.
 *
 * Funcionalidades:
 * - Muestra una BottomNavigation con distintas secciones: Inicio, Animales (favoritos), Cuenta
 *   y, si el usuario es admin, una sección extra de Administración.
 * - Gestiona el botón de atrás con [BackHandler] para volver a la pestaña inicial o navegar
 *   dentro del subflujo de administración.
 * - Solicita permiso de ubicación y, si se concede, ordena animales por proximidad mediante
 *   [HomeViewModel.sortByProximity].
 * - Escucha eventos del ViewModel (por ejemplo errores) y los muestra con [Toast].
 *
 * Arquitectura:
 * - MVVM + Compose.
 * - [HomeViewModel] se inyecta con Hilt.
 * - El estado se consume con `collectAsState()` y la UI reacciona automáticamente.
 *
 * @param viewModel ViewModel principal del Home (inyectado por Hilt por defecto).
 * @param onToggleDarkMode Callback para alternar tema oscuro/claro.
 * @param isDarkMode Indica si el tema actual es oscuro.
 * @param onBack Callback de navegación atrás (cuando ya estás en la pestaña raíz).
 * @param onLogout Callback para cerrar sesión y navegar fuera del Home.
 * @param onOpenEditProfile Callback para abrir edición de perfil.
 * @param onMyAnimals Callback para abrir "Mis animales" (voluntario).
 * @param onOpenChangePassword Callback para abrir cambio de contraseña.
 * @param onAnimalClick Callback para abrir el detalle de un animal.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onToggleDarkMode: () -> Unit,
    isDarkMode: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onMyAnimals: () -> Unit,
    onOpenChangePassword: () -> Unit,
    onAnimalClick: (String) -> Unit
) {
    /** Estado global de la pantalla obtenido desde el ViewModel (perfil, rol, etc.). */
    val state by viewModel.uiState.collectAsState()

    /**
     * Índice de la pestaña seleccionada (BottomNavigation):
     * 0=Inicio, 1=Animales, 2=Cuenta, 3=Admin (si aplica)
     */
    var selectPage by rememberSaveable { mutableIntStateOf(0) }

    /**
     * Subpágina dentro de la sección Admin.
     * 0=Dashboard admin, 1=Add animal, 2=Manage animals (actualmente no implementado en el when).
     */
    var adminPage by rememberSaveable { mutableIntStateOf(0) }

    /** Contexto Android necesario para permisos, Toasts, y location client. */
    val context = LocalContext.current

    /**
     * Cliente de localización (FusedLocationProviderClient).
     * Se guarda con remember para no recrearlo en recomposiciones.
     */
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    /**
     * Colores personalizados para items de la barra inferior (Material3).
     */
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        indicatorColor = MaterialTheme.colorScheme.secondaryContainer
    )

    /**
     * Manejo del botón atrás:
     * - Si estás en Admin y dentro de una subpágina (adminPage != 0), vuelve al dashboard admin.
     * - Si estás en cualquier pestaña distinta a Inicio, vuelve a Inicio.
     * - Si ya estás en Inicio, ejecuta el callback [onBack].
     */
    BackHandler {
        when {
            selectPage == 3 && adminPage != 0 -> adminPage = 0
            selectPage != 0 -> selectPage = 0
            else -> onBack()
        }
    }

    /**
     * Launcher para pedir permiso de ubicación en tiempo de ejecución.
     * Si se concede, intenta obtener la última localización conocida y ordenar por proximidad.
     */
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

    /**
     * Obtiene la última ubicación conocida si el permiso ya está concedido
     * y ordena animales por proximidad.
     *
     * Se usa como ayuda para el LaunchedEffect inicial.
     */
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

    /**
     * Solicitud de permiso de ubicación al entrar en la pantalla.
     * - Si ya hay permiso: obtiene ubicación y ordena.
     * - Si no: lanza el diálogo de permiso.
     */
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) getLocationAndOrganise()
        else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Observa eventos emitidos por el ViewModel y muestra errores con Toast.
     * (Patrón típico: SharedFlow de eventos one-shot.)
     */
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Error -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    /** Determina si el usuario actual tiene rol de administrador. */
    val isAdmin = state.userRole == UserRole.ADMIN

    /**
     * Scaffold principal:
     * - Top bar con saludo/nombre.
     * - Bottom bar con navegación entre secciones.
     * - Body: muestra la pantalla correspondiente según [selectPage].
     */
    Scaffold(
        topBar = { MyTopAppBar(name = state.username) },
        bottomBar = {
            /**
             * Contenedor de la barra inferior con estilo redondeado y elevación.
             */
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

                    /**
                     * Pestaña Admin solo visible para usuarios con rol ADMIN.
                     */
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
        /**
         * Contenido según la pestaña seleccionada.
         */
        when (selectPage) {
            0 -> HomeView(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel,
                onDetails = onAnimalClick
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
                onMyAnimals = onMyAnimals,
                profilePhoto = state.photo,
                userRole = state.userRole,
                viewModel = viewModel
            )

            3 -> {
                /**
                 * Subnavegación interna dentro de Admin.
                 */
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
