package com.gromber05.peco.ui.screens.register

/**
 * Representa el estado de la interfaz de usuario en la pantalla de registro.
 *
 * Contiene los valores introducidos en el formulario (nombre, email, contraseña,
 * confirmación y teléfono), junto con flags para controlar el comportamiento
 * de la UI durante el proceso de registro:
 * - Visibilidad de contraseña.
 * - Estado de carga.
 * - Resultado de registro completado.
 * - Mensaje de error para validación o fallos del backend.
 *
 * Esta clase suele ser gestionada por un ViewModel (por ejemplo, `RegisterViewModel`)
 * y expuesta a la UI mediante `StateFlow` para que Jetpack Compose reaccione a los cambios.
 *
 * @property name Nombre de usuario (o nombre real) introducido en el formulario.
 * @property email Correo electrónico introducido por el usuario.
 * @property pass Contraseña introducida por el usuario.
 * @property confirmPass Confirmación de la contraseña. Normalmente se valida que coincida con [pass].
 * @property isPasswordVisible Controla si la contraseña se muestra en texto plano o se oculta en el campo.
 * @property isLoading Indica si se está ejecutando el proceso de registro.
 * @property isRegistered Indica si el registro se ha completado correctamente.
 * @property error Mensaje de error a mostrar en la UI si la validación o el registro fallan.
 * Es `null` cuando no hay errores.
 * @property phone Número de teléfono introducido por el usuario (habitualmente opcional según la lógica de la app).
 */
data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val confirmPass: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null,
    val phone: String = ""
)
