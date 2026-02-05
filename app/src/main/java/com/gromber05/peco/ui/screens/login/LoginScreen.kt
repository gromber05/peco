package com.gromber05.peco.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Pantalla de inicio de sesión de la aplicación PECO.
 *
 * Esta pantalla permite al usuario autenticarse introduciendo su correo electrónico
 * y contraseña. Está desarrollada con Jetpack Compose y Material 3, y utiliza un
 * [LoginViewModel] inyectado mediante Hilt para gestionar el estado y la lógica del login.
 *
 * Funcionalidades principales:
 * - Introducción de correo electrónico y contraseña.
 * - Mostrar u ocultar la contraseña.
 * - Ejecución del proceso de inicio de sesión.
 * - Visualización de errores de autenticación.
 * - Indicador de carga durante el proceso de login.
 * - Navegación automática a la pantalla principal cuando el usuario está autenticado.
 * - Navegación a la pantalla de registro de usuarios.
 * - Navegación a la pantalla de recuperación de contraseña.
 * - Cambio del tema de la aplicación mediante un botón flotante.
 *
 * @param viewModel ViewModel encargado de la lógica y el estado del login.
 *                  Se obtiene por defecto mediante [hiltViewModel].
 * @param onNavigateToHome Callback que se ejecuta cuando el login es correcto.
 * @param onNavigateToRegister Callback para navegar a la pantalla de registro.
 * @param onNavigateToResetPassword Callback para navegar a la pantalla de recuperación de contraseña.
 * @param onToggleTheme Callback que alterna el tema de la aplicación.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToResetPassword: () -> Unit,
    onToggleTheme: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    /**
     * Observa el estado de autenticación del usuario.
     *
     * Cuando el estado `isLoggedIn` pasa a ser verdadero, se navega
     * automáticamente a la pantalla principal de la aplicación.
     */
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onNavigateToHome()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        floatingActionButton = {
            /**
             * Botón flotante encargado de alternar el tema de la aplicación.
             */
            FloatingActionButton(
                onClick = { onToggleTheme() },
                modifier = Modifier
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Cambiar Tema"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Pets,
                contentDescription = "Logo Protectora",
                modifier = Modifier.size(70.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Peco",
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo Electrónico") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.pass,
                onValueChange = { viewModel.onPassChange(it) },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    Text("Iniciando sesión...")
                } else {
                    Text("Iniciar sesión")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    onNavigateToRegister()
                }
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    onNavigateToResetPassword()
                }
            ) {
                Text(
                    text = "He olvidado mi contraseña",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

    }
}

@Preview(
    showBackground = true
)
@Composable
fun Preview_loginScreen() {
    LoginScreen(
        viewModel = hiltViewModel(),
        onNavigateToHome = {},
        onNavigateToRegister = {},
        onToggleTheme = {},
        onNavigateToResetPassword = {},
    )
}