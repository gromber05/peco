package com.gromber05.peco.data.remote

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataSource encargado de gestionar la autenticación de usuarios mediante FirebaseAuth.
 *
 * Responsabilidades:
 * - Comprobar si hay un usuario autenticado.
 * - Exponer el UID del usuario actual como [Flow].
 * - Gestionar inicio de sesión, registro y cierre de sesión.
 * - Permitir el cambio de contraseña y la recuperación mediante email.
 *
 * Arquitectura:
 * - Forma parte de la capa de datos (data/remote).
 * - Encapsula el acceso directo a FirebaseAuth para desacoplarlo
 *   del resto de la aplicación (ViewModels / Repositories).
 *
 * Uso de Flows:
 * - Se utiliza [callbackFlow] para transformar el listener de FirebaseAuth
 *   en un flujo reactivo que emite cambios de autenticación en tiempo real.
 */
@Singleton
class AuthFirestoreDataSource @Inject constructor(
    /** Instancia singleton de FirebaseAuth inyectada mediante Hilt. */
    private val auth: FirebaseAuth
) {

    /**
     * Indica si existe un usuario autenticado actualmente.
     *
     * @return `true` si hay un usuario logueado, `false` en caso contrario.
     */
    fun isLoggedIn(): Boolean = auth.currentUser != null

    /**
     * Expone el UID del usuario autenticado como un [Flow].
     *
     * Funcionamiento:
     * - Emite inmediatamente el UID actual (o null).
     * - Se suscribe a los cambios de estado de autenticación mediante
     *   [FirebaseAuth.AuthStateListener].
     * - Cada vez que cambia el usuario, se emite el nuevo UID.
     *
     * @return [Flow] que emite el UID del usuario o `null` si no hay sesión activa.
     */
    fun currentUidFlow(): Flow<String?> = callbackFlow {
        // Emisión inicial con el estado actual
        trySend(auth.currentUser?.uid)

        // Listener de cambios de autenticación
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }

        auth.addAuthStateListener(listener)

        // Se elimina el listener cuando el Flow se cancela
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    /**
     * Envía un correo de recuperación de contraseña al email indicado.
     *
     * Nota:
     * - Firebase gestiona automáticamente el envío del correo.
     *
     * @param email Email del usuario que solicita el reseteo de contraseña.
     */
    fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    /**
     * Inicia sesión con email y contraseña.
     *
     * @param email Email del usuario.
     * @param password Contraseña del usuario.
     *
     * @throws Exception si las credenciales son incorrectas o hay error de red.
     */
    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    /**
     * Registra un nuevo usuario con email y contraseña.
     *
     * @param email Email del nuevo usuario.
     * @param password Contraseña del nuevo usuario.
     * @return UID del usuario recién creado.
     *
     * @throws Exception si el registro falla (email inválido, ya existente, etc.).
     */
    suspend fun signUp(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user!!.uid
    }

    /**
     * Cambia la contraseña del usuario actualmente autenticado.
     *
     * Flujo:
     * 1) Se obtiene el usuario actual.
     * 2) Se reautentica con la contraseña actual (requisito de seguridad de Firebase).
     * 3) Se actualiza la contraseña con el nuevo valor.
     *
     * @param currentPassword Contraseña actual del usuario.
     * @param newPassword Nueva contraseña a establecer.
     *
     * @throws IllegalStateException si no hay usuario logueado o no tiene email.
     */
    suspend fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser ?: throw IllegalStateException("No hay usuario logueado")
        val email = user.email ?: throw IllegalStateException("El usuario no tiene email")

        // Credenciales para reautenticación
        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential).await()
        user.updatePassword(newPassword).await()
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Expone el estado de autenticación como un [Flow] booleano.
     *
     * - `true` si el usuario está logueado.
     * - `false` si no hay sesión activa.
     *
     * Usa [distinctUntilChanged] para evitar emisiones duplicadas
     * cuando el estado no cambia.
     *
     * @return [Flow] con el estado de autenticación.
     */
    fun isLoggedInFlow(): Flow<Boolean?> =
        currentUidFlow()
            .map { uid -> uid != null }
            .distinctUntilChanged()
}
