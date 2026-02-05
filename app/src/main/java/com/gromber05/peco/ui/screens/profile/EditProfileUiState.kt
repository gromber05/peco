package com.gromber05.peco.ui.screens.profile

/**
 * Representa el estado de la interfaz de usuario en la pantalla de edición de perfil.
 *
 * Contiene los datos editables del perfil (nombre de usuario y foto) y los flags
 * necesarios para controlar el comportamiento de la UI durante el guardado:
 * - Estado de carga para mostrar progreso y evitar acciones repetidas.
 * - Mensaje de error para informar de problemas de validación o de guardado.
 * - Bandera de éxito para notificar que el perfil se ha guardado correctamente.
 *
 * Esta clase suele ser gestionada por un ViewModel (por ejemplo, `EditProfileViewModel`)
 * y expuesta a la UI mediante `StateFlow` para que Jetpack Compose reaccione a los cambios.
 *
 * @property username Nombre de usuario que se muestra y se edita en el formulario.
 * @property photo URI (en formato `String`) de la foto de perfil seleccionada.
 * Puede ser una cadena vacía si no hay foto o si se ha eliminado.
 * @property isLoading Indica si se está ejecutando una operación de guardado/actualización.
 * @property error Mensaje de error a mostrar en la UI. Es `null` cuando no hay errores.
 * @property saved Indica si el perfil se ha guardado correctamente (por ejemplo, para mostrar un Toast o navegar).
 */
data class EditProfileUiState(
    val username: String = "",
    val photo: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

