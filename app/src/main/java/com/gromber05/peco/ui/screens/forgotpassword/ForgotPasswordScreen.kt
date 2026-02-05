package com.gromber05.peco.ui.screens.forgotpassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Pantalla de recuperación de contraseña.
 *
 * Permite al usuario solicitar el envío de un correo electrónico para
 * restablecer su contraseña mediante Firebase Authentication.
 *
 * Funcionalidades:
 * - Introducción del email del usuario.
 * - Validación visual del estado (cargando, error o éxito).
 * - Envío del correo de recuperación.
 * - Navegación hacia atrás mediante la TopAppBar.
 *
 * Arquitectura:
 * - Sigue patrón MVVM.
 * - El estado se consume desde [ForgotPasswordViewModel.uiState].
 * - Las acciones de usuario delegan la lógica al ViewModel.
 *
 * @param onBack Callback para volver a la pantalla anterior.
 * @param viewModel ViewModel inyectado por Hilt que gestiona la lógica de negocio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    /** Estado de UI observado desde el ViewModel. */
    val state by viewModel.uiState.collectAsState()

    /**
     * Scaffold principal de la pantalla.
     * Incluye una TopAppBar con navegación hacia atrás.
     */
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Recuperar contraseña") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        /**
         * Contenido principal de la pantalla.
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /**
             * Texto informativo que explica el proceso de recuperación.
             */
            Text(
                "Introduce tu correo y te enviaremos un enlace para restablecer la contraseña.",
                style = MaterialTheme.typography.bodyMedium
            )

            /**
             * Campo de texto para introducir el correo electrónico.
             */
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Correo") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            /**
             * Mensaje de error mostrado si ocurre algún problema.
             */
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            /**
             * Mensaje de éxito mostrado cuando el correo se ha enviado correctamente.
             */
            if (state.success) {
                Text(
                    "Correo enviado. Revisa tu bandeja de entrada (y spam).",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            /**
             * Botón para enviar el correo de recuperación.
             *
             * - Se deshabilita mientras se está enviando.
             * - Muestra un indicador de carga durante el proceso.
             */
            Button(
                onClick = viewModel::sendReset,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Enviando…")
                } else {
                    Text("Enviar enlace")
                }
            }
        }
    }
}
