package com.gromber05.peco.data.repository

import com.gromber05.peco.data.remote.AuthFirestoreDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio encargado de gestionar la autenticación y el estado de la sesión de los usuarios.
 * Centraliza las operaciones de Firebase Auth y el flujo de identidad del usuario.
 *
 * @property db Fuente de datos remota ([AuthFirestoreDataSource]) para operaciones de autenticación.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val db: AuthFirestoreDataSource
) {
    /** * Verifica de forma síncrona si hay un usuario con sesión activa en el dispositivo.
     * @return true si el usuario está autenticado, false en caso contrario.
     */
    fun isLoggedIn(): Boolean = db.isLoggedIn()

    /** * Provee un flujo constante del Identificador Único (UID) del usuario actual.
     * Emite null si el usuario cierra sesión.
     */
    fun currentUidFlow(): Flow<String?> = db.currentUidFlow()

    /** * Inicia sesión en la aplicación utilizando credenciales de correo y contraseña.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña asociada.
     */
    suspend fun signIn(email: String, password: String) = db.signIn(email, password)

    /** * Registra una nueva cuenta de usuario en el sistema.
     * @param email Correo electrónico para la nueva cuenta.
     * @param password Contraseña de acceso.
     * @return El UID generado para el nuevo usuario.
     */
    suspend fun signUp(email: String, password: String): String = db.signUp(email, password)

    /** * Permite al usuario autenticado actualizar su contraseña actual por una nueva.
     * @param currentPassword Contraseña vigente para verificación de seguridad.
     * @param newPassword La nueva contraseña a establecer.
     */
    suspend fun changePassword(currentPassword: String, newPassword: String) = db.changePassword(currentPassword, newPassword)

    /** * Cierra la sesión activa del usuario en el dispositivo.
     */
    fun signOut() = db.signOut()

    /** * Expone un flujo que indica si el usuario está autenticado o no.
     * Útil para observar cambios de estado de sesión en tiempo real desde la UI.
     */
    fun isLoggedInFlow(): Flow<Boolean?> = db.isLoggedInFlow()

    /** * Envía un correo electrónico de recuperación de contraseña a la dirección especificada.
     * Aplica un [String.trim] automáticamente al correo para evitar errores de espacios.
     * @param email Dirección de correo destino.
     */
    fun sendPasswordResetEmail(email: String) = db.sendPasswordResetEmail(email.trim())
}