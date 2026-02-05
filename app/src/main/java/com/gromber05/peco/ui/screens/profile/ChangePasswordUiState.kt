package com.gromber05.peco.ui.screens.profile

/**
 * Representa el estado de la interfaz de usuario en la pantalla de cambio de contraseña.
 *
 * Contiene los valores del formulario (contraseña actual, nueva y confirmación),
 * así como indicadores para controlar la UI durante el proceso de guardado:
 * - Estado de carga para mostrar progreso y bloquear acciones.
 * - Mensaje de error para informar al usuario de fallos de validación o backend.
 * - Bandera de éxito para notificar que la contraseña se ha actualizado correctamente.
 *
 * Esta clase suele ser gestionada por un ViewModel (por ejemplo, `ChangePasswordViewModel`)
 * y expuesta a la UI mediante `StateFlow` para que Jetpack Compose reaccione a los cambios.
 *
 * @property current Contraseña actual introducida por el usuario.
 * @property newPass Nueva contraseña introducida por el usuario.
 * @property confirm Confirmación de la nueva contraseña. Normalmente se valida que coincida con [newPass].
 * @property isLoading Indica si se está ejecutando la operación de cambio de contraseña.
 * @property error Mensaje de error a mostrar en la UI si ocurre algún problema. Es `null` cuando no hay errores.
 * @property saved Indica si la contraseña se ha cambiado correctamente (por ejemplo, para lanzar un Toast o navegar).
 */
data class ChangePasswordUiState(
    val current: String = "",
    val newPass: String = "",
    val confirm: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

