package com.gromber05.peco.ui.screens.login

/**
 * Representa el estado de la interfaz de usuario en la pantalla de inicio de sesión.
 *
 * Esta clase sigue el patrón de *state hoisting* típico en arquitecturas como
 * MVVM o MVI, y se utiliza normalmente junto a un `ViewModel` para reflejar
 * de forma reactiva el estado actual del proceso de login.
 *
 * Contiene tanto los datos introducidos por el usuario como los flags de control
 * necesarios para gestionar la UI (carga, errores, visibilidad de contraseña
 * y roles del usuario).
 *
 * @property user Nombre de usuario introducido por el usuario.
 * @property email Dirección de correo electrónico introducida en el formulario.
 * @property pass Contraseña introducida por el usuario.
 * @property isLoading Indica si el proceso de autenticación está en curso.
 * Se usa normalmente para mostrar un indicador de carga y bloquear la interacción.
 * @property error Mensaje de error a mostrar en la UI si el login falla.
 * Es `null` cuando no hay errores.
 * @property isPasswordVisible Controla si la contraseña se muestra en texto plano
 * o se oculta en el campo de entrada.
 * @property isVolunteer Indica si el usuario ha seleccionado o tiene el rol de voluntario.
 * @property isLoggedIn Indica si el usuario ha iniciado sesión correctamente.
 * @property isAdmin Indica si el usuario autenticado tiene permisos de administrador.
 */
data class LoginUiState(
    val user: String = "",
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPasswordVisible: Boolean = false,
    val isVolunteer: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false
)
