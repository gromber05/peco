package com.gromber05.peco.ui.screens.forgotpassword

/**
 * Estado de UI para la pantalla de recuperación de contraseña.
 *
 * Representa de forma inmutable el estado necesario para la pantalla:
 * - Email introducido por el usuario.
 * - Estado de carga durante el envío del correo.
 * - Indicador de éxito cuando el correo se ha enviado correctamente.
 * - Mensaje de error si ocurre algún problema.
 *
 * Este estado es producido por el ViewModel y consumido por Jetpack Compose,
 * que recompondrá la UI automáticamente cuando cambien sus valores.
 */
data class ForgotPasswordUiState(

    /**
     * Correo electrónico introducido por el usuario.
     */
    val email: String = "",

    /**
     * Indica si se está procesando el envío del correo de recuperación.
     *
     * Cuando es `true`, la UI suele deshabilitar el botón y mostrar un spinner.
     */
    val isLoading: Boolean = false,

    /**
     * Indica si el correo de recuperación se ha enviado correctamente.
     *
     * Cuando es `true`, la UI puede mostrar un mensaje informativo al usuario.
     */
    val success: Boolean = false,

    /**
     * Mensaje de error a mostrar en la UI.
     *
     * Si es `null`, no hay errores activos.
     */
    val error: String? = null
)
