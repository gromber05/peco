package com.gromber05.peco.model

/**
 * Representa los diferentes estados en los que se puede encontrar un animal
 * durante su proceso en la plataforma de adopción.
 * * @property value Representación textual amigable en español para mostrar en la interfaz de usuario.
 */
enum class AdoptionState(val value : String) {
    /** El animal está listo para ser visto y deslizado por los usuarios. */
    AVAILABLE("Disponible"),

    /** El animal está en un proceso intermedio o bajo revisión. */
    PENDING("Pendiente"),

    /** Se ha iniciado un proceso de adopción formal y el animal ya no está disponible para otros. */
    RESERVED("Reservado/a"),

    /** El proceso ha finalizado con éxito y el animal tiene un nuevo hogar. */
    ADOPTED("Adoptado/a"),
}