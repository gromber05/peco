package com.gromber05.peco.model.user

/**
 * Representa la entidad de Usuario dentro del sistema.
 * Contiene la información de perfil, contacto y permisos necesaria para la
 * personalización de la experiencia y la gestión de roles.
 *
 * @property uid Identificador único de Firebase Authentication vinculado a este perfil.
 * @property username Nombre público elegido por el usuario.
 * @property email Dirección de correo electrónico asociada a la cuenta.
 * @property photo URL de la imagen de perfil (proveniente de Firebase Storage o Auth). Puede ser null.
 * @property role Nivel de acceso y permisos del usuario (definido en [UserRole]).
 * @property phone Número de teléfono de contacto para procesos de adopción o gestión.
 */
data class User(
    val uid: String,
    val username: String,
    val email: String,
    val photo: String?,
    val role: UserRole,
    val phone: String
)