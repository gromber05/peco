package com.gromber05.peco.ui.screens.admin

import com.gromber05.peco.model.data.LabelCount

/**
 * Estado de UI del panel de administración (dashboard).
 *
 * Representa de forma inmutable toda la información necesaria para mostrar
 * estadísticas globales de la aplicación en la pantalla de administración.
 *
 * Este estado suele ser producido por un ViewModel y consumido por la UI
 * mediante Compose, reaccionando automáticamente a los cambios.
 *
 * Contenido del estado:
 * - Contadores globales de animales por estado.
 * - Métricas de interacción (likes y dislikes).
 * - Distribuciones y rankings por especie.
 * - Filtros activos y mensajes de error.
 */
data class AdminDashboardUiState(
    /** Número total de animales registrados en el sistema. */
    val totalAnimals: Int = 0,

    /** Número de animales disponibles para adopción. */
    val available: Int = 0,

    /** Número de animales ya adoptados. */
    val adopted: Int = 0,

    /** Número de animales en estado pendiente. */
    val pending: Int = 0,

    /** Número total de interacciones positivas (LIKE). */
    val likes: Int = 0,

    /** Número total de interacciones negativas (DISLIKE). */
    val dislikes: Int = 0,

    /**
     * Distribución de animales por especie.
     *
     * Cada elemento de la lista representa:
     * - label: nombre de la especie
     * - count: número de animales de esa especie
     */
    val bySpecies: List<LabelCount> = emptyList(),

    /**
     * Ranking de especies con más likes.
     *
     * Lista ordenada normalmente de forma descendente por número de likes.
     */
    val topLikedSpecies: List<LabelCount> = emptyList(),

    /**
     * Filtro de especie actualmente seleccionado en el dashboard.
     *
     * Si es `null`, no hay ningún filtro activo.
     */
    val selectedSpeciesFilter: String? = null,

    /**
     * Mensaje de error a mostrar en la UI.
     *
     * Si es `null`, no hay errores activos.
     */
    val error: String? = null
)
