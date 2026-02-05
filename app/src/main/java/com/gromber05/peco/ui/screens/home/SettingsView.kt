package com.gromber05.peco.ui.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.gromber05.peco.model.user.UserRole


/**
 * Vista de ajustes / cuenta dentro de [HomeScreen].
 *
 * Muestra información del usuario (nombre, email, rol y foto) y ofrece acciones:
 * - Alternar modo oscuro.
 * - Abrir edición de perfil.
 * - Abrir cambio de contraseña.
 * - Acceder a "Mis animales" (solo VOLUNTEER o ADMIN).
 * - Cerrar sesión con confirmación.
 *
 * Además incluye selector de imagen (picker) para elegir un avatar local y enviar
 * los bytes al [HomeViewModel] mediante [HomeViewModel.onPhotoSelected].
 *
 * Nota:
 * - Este composable recibe el [HomeViewModel] por parámetro para reutilizar lógica
 *   ya existente y evitar duplicar estado aquí.
 *
 * @param modifier Modificador externo para personalizar layout.
 * @param username Nombre visible del usuario.
 * @param email Correo del usuario.
 * @param userRole Rol del usuario (USER/ADMIN/VOLUNTEER) para UI condicional.
 * @param profilePhoto URL de la foto de perfil (puede ser null).
 * @param isDarkMode Indica si el tema actual es oscuro.
 * @param onToggleTheme Callback para alternar tema.
 * @param onLogout Callback para cerrar sesión.
 * @param onOpenEditProfile Callback para navegar a editar perfil.
 * @param onOpenChangePassword Callback para navegar a cambiar contraseña.
 * @param onMyAnimals Callback para abrir la sección "Mis animales".
 * @param viewModel ViewModel principal de Home (para acciones como selección de foto).
 */
@Composable
fun SettingsView(
    modifier: Modifier = Modifier,
    username: String,
    email: String,
    userRole: UserRole = UserRole.USER,
    profilePhoto: String?,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onLogout: () -> Unit,
    onOpenEditProfile: () -> Unit = {},
    onOpenChangePassword: () -> Unit = {},
    onMyAnimals: () -> Unit,
    viewModel: HomeViewModel,
) {
    /** Controla la visibilidad del diálogo de confirmación de logout. */
    var showLogoutDialog by remember { mutableStateOf(false) }

    /** Contexto Android necesario para abrir InputStream del picker. */
    val context = LocalContext.current

    /**
     * Diálogo de confirmación para cerrar sesión.
     * Evita acciones accidentales.
     */
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") }
            }
        )
    }


    /**
     * Selector de imagen del sistema (Photo Picker) para elegir avatar.
     *
     * - Si el usuario selecciona una imagen, se leen los bytes desde ContentResolver.
     * - Se delega al ViewModel con `onPhotoSelected(bytes, uriString)`.
     *
     * Nota:
     * - Esto solo guarda los bytes/uri en el estado; la subida a backend normalmente
     *   se realiza después (por ejemplo al guardar perfil).
     */
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val bytes = context.contentResolver.openInputStream(uri)!!.use { it.readBytes() }
                viewModel.onPhotoSelected(bytes, uri.toString())
            }
        }
    )

    /**
     * Listado vertical tipo "settings" con secciones y filas.
     */
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        /**
         * Tarjeta de cabecera con información del usuario:
         * - Avatar (icono o imagen).
         * - Username y email.
         * - Chip con rol.
         * - Botón de editar perfil.
         */
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (profilePhoto == null) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            } else {
                                AsyncImage(
                                    model = profilePhoto,
                                    contentDescription = "Tu foto",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = username.ifBlank { "Usuario" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = email.ifBlank { "—" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(8.dp))

                        /**
                         * Chip de rol: informa al usuario y sirve como feedback visual.
                         */
                        when (userRole) {
                            UserRole.USER -> {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Usuario") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Pets,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                            UserRole.ADMIN -> {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Administrador") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                            UserRole.VOLUNTEER -> {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Voluntario") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }

                    /**
                     * Botón para editar perfil.
                     */
                    IconButton(onClick = onOpenEditProfile) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar perfil")
                    }
                }
            }
        }

        item {
            SettingsSection(title = "Apariencia")
        }

        /**
         * Ajuste de tema: switch para modo oscuro.
         */
        item {
            SettingsRow(
                title = "Modo oscuro",
                subtitle = "Cambia el tema de la aplicación",
                leading = {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = null
                    )
                },
                trailing = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = {
                            onToggleTheme()
                        }
                    )
                }
            )
        }

        item {
            SettingsSection(title = "Cuenta")
        }

        /**
         * Navegación a cambio de contraseña.
         */
        item {
            SettingsRow(
                title = "Cambiar contraseña",
                subtitle = "Actualiza tu contraseña de acceso",
                leading = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                trailing = {
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                },
                onClick = onOpenChangePassword
            )
        }

        /**
         * Sección "Mis animales" disponible solo para VOLUNTEER o ADMIN.
         */
        if (userRole == UserRole.VOLUNTEER || userRole == UserRole.ADMIN) {
            item {
                SettingsSection(title = "Mis animales")
            }

            item {
                SettingsRow(
                    title = "Mis animales",
                    subtitle = "Gestiona tus animales",
                    leading = {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                        )
                    },
                    onClick = { onMyAnimals() }
                )
            }
        }

        item {
            SettingsSection(title = "Sesión")
        }

        /**
         * Opción de cerrar sesión: muestra diálogo de confirmación.
         */
        item {
            SettingsRow(
                title = "Cerrar sesión",
                subtitle = "Salir de tu cuenta en este dispositivo",
                leading = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                titleColor = MaterialTheme.colorScheme.error,
                onClick = { showLogoutDialog = true }
            )
        }

        item { Spacer(Modifier.height(10.dp)) }
    }
}

/**
 * Composable auxiliar para mostrar un título de sección en la pantalla de ajustes.
 *
 * @param title Texto de la sección (ej. "Cuenta", "Apariencia", "Sesión").
 */
@Composable
private fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 6.dp)
    )
}

/**
 * Fila reutilizable para una opción de ajustes.
 *
 * Permite componer:
 * - Icono/elemento leading (izquierda).
 * - Texto principal y opcionalmente subtítulo (centro).
 * - Elemento trailing (derecha), por ejemplo chevron o switch.
 *
 * La fila puede ser clickable si [onClick] no es null.
 *
 * @param title Título principal de la opción.
 * @param subtitle Texto secundario opcional.
 * @param leading Contenido composable opcional a la izquierda.
 * @param trailing Contenido composable opcional a la derecha.
 * @param titleColor Color del título (por defecto color de surface).
 * @param onClick Acción opcional al pulsar la fila.
 */
@Composable
private fun SettingsRow(
    title: String,
    subtitle: String? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null
) {
    /**
     * Modificador de la fila:
     * - Ancho completo, forma redondeada.
     * - Clickable solo si hay onClick.
     * - Padding interno.
     */
    val rowMod = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        )
        .padding(horizontal = 14.dp, vertical = 12.dp)

    /**
     * Contenedor Surface para dar fondo y ligera elevación.
     */
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = rowMod,
            verticalAlignment = Alignment.CenterVertically
        ) {
            /**
             * Leading opcional: normalmente un icono dentro de una cajita.
             */
            if (leading != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        modifier = Modifier.padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) { leading() }
                }
                Spacer(Modifier.width(12.dp))
            }

            /**
             * Texto principal y subtítulo.
             */
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = titleColor,
                    fontWeight = FontWeight.SemiBold
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /**
             * Trailing opcional: switch, chevron, etc.
             */
            if (trailing != null) {
                Spacer(Modifier.width(10.dp))
                trailing()
            }
        }
    }
}
