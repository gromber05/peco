package com.gromber05.peco.model.user

/**
 * Define los niveles de autoridad y permisos disponibles en la plataforma.
 * Se utiliza para restringir o habilitar funcionalidades específicas según el perfil del usuario.
 */
enum class UserRole {
    /** * Usuario estándar. Puede ver animales, realizar búsquedas y gestionar sus favoritos (likes).
     */
    USER,

    /** * Colaborador con permisos para gestionar animales, registrar nuevas entradas y supervisar procesos.
     */
    VOLUNTEER,

    /** * Superusuario con acceso total a estadísticas globales, gestión de usuarios y configuración del sistema.
     */
    ADMIN
}