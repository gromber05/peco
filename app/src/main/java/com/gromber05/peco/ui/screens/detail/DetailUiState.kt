package com.gromber05.peco.ui.screens.detail

import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.model.user.User

/**
 * Estado de UI para la pantalla de detalle de un animal.
 *
 * Representa de forma inmutable toda la información necesaria para que la UI muestre:
 * - Indicador de carga mientras se recuperan datos.
 * - El animal encontrado (si existe).
 * - Un estado "notFound" para manejar IDs inválidos o documentos borrados.
 * - Información del voluntario asociado (si se decide mostrar en la pantalla).
 *
 * Este estado es producido por el ViewModel y consumido por Jetpack Compose,
 * que recompondrá la interfaz automáticamente cuando cambien sus valores.
 */
data class DetailUiState(

    /**
     * Indica si la pantalla está cargando los datos.
     *
     * Cuando es `true`, la UI suele mostrar un spinner o placeholder.
     */
    val isLoading: Boolean = true,

    /**
     * Animal a mostrar en el detalle.
     *
     * Será `null` cuando:
     * - Aún se está cargando.
     * - No existe el documento en Firestore.
     * - Ha ocurrido un error y no se ha podido obtener.
     */
    val animal: Animal? = null,

    /**
     * Indica que el animal solicitado no existe o no se ha encontrado.
     *
     * Se usa normalmente para:
     * - Mostrar un mensaje de "no encontrado".
     * - O navegar hacia atrás automáticamente desde la pantalla de detalle.
     */
    val notFound: Boolean = false,

    /**
     * Voluntario asociado al animal (por ejemplo el usuario que lo ha subido o gestiona).
     *
     * Puede usarse para mostrar información adicional:
     * - Nombre del voluntario
     * - Datos de contacto
     *
     * Si es `null`, significa que no se ha cargado o no se usa en la UI actual.
     */
    val volunteer: User? = null
)
