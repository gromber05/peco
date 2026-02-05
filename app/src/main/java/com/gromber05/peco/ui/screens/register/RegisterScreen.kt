package com.gromber05.peco.ui.screens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Pantalla de registro de usuario.
 *
 * Permite crear una cuenta nueva introduciendo los datos del formulario:
 * - Nombre completo
 * - Correo electrónico
 * - Número de teléfono
 * - Contraseña y confirmación
 *
 * La pantalla observa el estado expuesto por [RegisterViewModel] mediante `StateFlow`
 * y reacciona a los cambios para:
 * - Mostrar errores de validación o backend.
 * - Mostrar indicador de carga durante el registro.
 * - Notificar el éxito del registro ejecutando [onRegisterSuccess].
 *
 * Incluye además un botón flotante (FAB) para alternar el tema (claro/oscuro).
 *
 * @param viewModel Instancia de [RegisterViewModel]. Por defecto se obtiene con [hiltViewModel]
 * para inyección con Hilt.
 * @param onNavigateBack Callback para volver a la pantalla anterior (por ejemplo, login).
 * @param onRegisterSuccess Callback que se ejecuta cuando el registro finaliza con éxito.
 * @param onToggleTheme Callback para alternar el tema de la aplicación.
 * @param isDarkMode Indica si el tema actual es oscuro. (En esta implementación se recibe
 * como parámetro, aunque la UI no lo utiliza directamente).
 */
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onToggleTheme: () -> Unit,
    isDarkMode: Boolean
) {
    val state by viewModel.uiState.collectAsState()

    /**
     * Efecto que reacciona al cambio de `state.isRegistered`.
     *
     * Cuando el registro se completa correctamente (`isRegistered == true`),
     * se invoca [onRegisterSuccess] para navegar o actualizar el flujo de la app.
     */
    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        /**
         * Botón flotante que permite alternar el tema (modo claro/oscuro).
         */
        floatingActionButton = {
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /**
             * Icono principal de la pantalla (branding / logo).
             */
            Icon(
                imageVector = Icons.Filled.Pets,
                contentDescription = "Logo Protectora",
                modifier = Modifier.size(70.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Título de la pantalla.
             */
            Text(
                text = "Crear Cuenta",
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            /**
             * Campo: nombre completo.
             */
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Campo: correo electrónico.
             */
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

            /**
             * Campo: número de teléfono.
             */
            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                label = { Text("Número de teléfono") },
                leadingIcon = { Icon(Icons.Filled.Call, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Campo: contraseña.
             *
             * Incluye icono para alternar la visibilidad, reutilizando `state.isPasswordVisible`.
             */
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Campo: confirmación de contraseña.
             *
             * Se marca como error cuando no coincide con `state.pass` y el usuario ya ha escrito algo.
             */
            OutlinedTextField(
                value = state.confirmPass,
                onValueChange = { viewModel.onConfirmPassChange(it) },
                label = { Text("Confirmar contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = state.pass != state.confirmPass && state.confirmPass.isNotEmpty()
            )

            /**
             * Mensaje de error general del proceso de registro (validación o backend).
             */
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            /**
             * Botón principal de registro.
             *
             * Durante la carga se muestra un [CircularProgressIndicator] y el botón se deshabilita
             * para evitar envíos múltiples.
             */
            Button(
                onClick = { viewModel.register() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Acción secundaria para navegar a la pantalla de login si el usuario ya tiene cuenta.
             */
            TextButton(onClick = onNavigateBack) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Previsualización de la pantalla de registro en Android Studio.
 *
 * Nota: en esta preview se utilizan `TODO()` para callbacks y ViewModel,
 * lo cual provoca error si se ejecuta tal cual. Se mantiene sin modificar
 * para respetar el código original.
 */
@Preview(showBackground = true)
@Composable
fun Preview_RegisterScreen() {
    RegisterScreen(
        onNavigateBack = {},
        viewModel = TODO(),
        onRegisterSuccess = TODO(),
        onToggleTheme = TODO(),
        isDarkMode = true
    )
}
