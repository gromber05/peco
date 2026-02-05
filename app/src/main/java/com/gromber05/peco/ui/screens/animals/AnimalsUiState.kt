package com.gromber05.peco.ui.screens.animals

import com.gromber05.peco.model.data.Animal

/**
 * Estado de UI para la pantalla de listado de animales.
 *
 * Representa de forma inmutable toda la información necesaria para que
 * la UI muestre correctamente el contenido según el estado actual:
 * carga, error, resultados vacíos o lista de animales.
 *
 * Este estado es producido por el ViewModel y consumido por la UI
 * mediante Jetpack Compose, reaccionando automáticamente a los cambios.
 */
data class AnimalsUiState(

    /**
     * Indica si los datos se están cargando actualmente.
     *
     * Cuando es `true`, la UI suele mostrar un indicador de progreso.
     */
    val isLoading: Boolean = true,

    /**
     * Indica que no se han encontrado resultados tras aplicar filtros
     * o realizar la consulta.
     *
     * Se usa para mostrar mensajes de tipo "no hay resultados".
     */
    val notFound: Boolean = false,

    /**
     * Mensaje de error a mostrar en la UI.
     *
     * Si es `null`, no hay errores activos.
     */
    val error: String? = null,

    /**
     * Lista de animales a mostrar en la pantalla.
     *
     * Se mantiene vacía cuando:
     * - Está cargando.
     * - No hay resultados.
     * - Ha ocurrido un error.
     */
    val animals: List<Animal> = emptyList(),

    /**
     * Indica si está activo un filtro en el listado.
     *
     * Por ejemplo:
     * - Mostrar solo animales propios del voluntario.
     * - Mostrar solo favoritos.
     */
    val filter: Boolean = false
)
