package com.gromber05.peco.model.events

/**
 * Representa los eventos unidireccionales que la Interfaz de Usuario (UI) debe reaccionar.
 * Al ser una [sealed interface], garantiza que todas las implementaciones posibles
 * sean conocidas en tiempo de compilación, facilitando el manejo exhaustivo mediante 'when'.
 */
sealed interface UiEvent {

    /**
     * Indica que el usuario ha cerrado sesión exitosamente.
     * Suele gatillar una navegación hacia la pantalla de Login y la limpieza de la pila de pantallas.
     */
    data object LoggedOut : UiEvent

    /**
     * Representa un fallo en alguna operación (ej. error de red, credenciales inválidas).
     * @property message El texto descriptivo del error para mostrar al usuario.
     */
    data class Error(val message: String) : UiEvent

    /**
     * Representa la finalización exitosa de una acción (ej. perfil actualizado, animal creado).
     * @property message El mensaje de confirmación para el usuario.
     */
    data class Success(val message: String) : UiEvent
}