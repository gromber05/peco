package com.gromber05.peco.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Pantalla de cambio de contraseña.
 *
 * Esta pantalla permite al usuario actualizar su contraseña introduciendo:
 * - La contraseña actual.
 * - La nueva contraseña.
 * - La confirmación de la nueva contraseña.
 *
 * El estado y la lógica de negocio se delegan en [ChangePasswordViewModel], que expone un [StateFlow]
 * observado mediante `collectAsState()`.
 *
 * Comportamiento principal:
 * - Muestra un [TopAppBar] con botón de volver.
 * - Presenta un formulario con tres campos de contraseña (enmascarados).
 * - Muestra un [LinearProgressIndicator] cuando la operación está en curso.
 * - Muestra un mensaje de error si `state.error` no es nulo.
 * - Lanza un [Toast] cuando la operación se completa correctamente (`state.saved == true`).
 *
 * @param onBack Callback que se ejecuta al pulsar “Volver” o “Cancelar”.
 * Normalmente navega a la pantalla anterior.
 * @param viewModel Instancia de [ChangePasswordViewModel]. Por defecto se obtiene mediante [hiltViewModel]
 * para inyección con Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    /**
     * Efecto que reacciona a cambios en `state.saved`.
     *
     * Cuando el cambio de contraseña finaliza con éxito (`saved == true`),
     * se muestra un [Toast] informativo al usuario.
     */
    LaunchedEffect(state.saved) {
        if (state.saved) Toast.makeText(context, "Contraseña cambiada", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cambiar contraseña") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /**
             * Indicador de carga superior para reflejar que se está ejecutando
             * una operación (por ejemplo, validación/actualización remota).
             */
            if (state.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            /**
             * Cabecera visual de la sección de seguridad.
             * Se utiliza un [Surface] con `surfaceVariant` para destacar el bloque.
             */
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text("Seguridad", style = MaterialTheme.typography.titleMedium)
                }
            }

            /**
             * Campo para introducir la contraseña actual.
             * Se muestra enmascarada con [PasswordVisualTransformation].
             */
            OutlinedTextField(
                value = state.current,
                onValueChange = viewModel::onCurrentChange,
                label = { Text("Contraseña actual") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            /**
             * Campo para introducir la nueva contraseña.
             * Se muestra enmascarada con [PasswordVisualTransformation].
             */
            OutlinedTextField(
                value = state.newPass,
                onValueChange = viewModel::onNewChange,
                label = { Text("Nueva contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            /**
             * Campo para confirmar la nueva contraseña.
             * Se muestra enmascarada con [PasswordVisualTransformation].
             */
            OutlinedTextField(
                value = state.confirm,
                onValueChange = viewModel::onConfirmChange,
                label = { Text("Confirmar nueva contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            /**
             * Mensaje de error visible en la UI si algo falla (validación o backend).
             */
            if (state.error != null) {
                Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
            }

            /**
             * Botón principal para guardar el cambio de contraseña.
             * Se deshabilita durante la carga para evitar múltiples envíos.
             */
            Button(
                onClick = viewModel::save,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            /**
             * Botón secundario para cancelar la operación y volver atrás.
             */
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}
