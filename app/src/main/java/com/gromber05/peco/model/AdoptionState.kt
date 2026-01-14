package com.gromber05.peco.model

enum class AdoptionState(val value : String) {
    AVAILABLE("Disponible"),        // Disponible para adoptar
    PENDING("Pendiente"),          // Solicitud enviada / en revisión
    RESERVED("Reservado/a"),         // Reservado
    ADOPTED("Adoptado/a"),          // Adoptado
}
