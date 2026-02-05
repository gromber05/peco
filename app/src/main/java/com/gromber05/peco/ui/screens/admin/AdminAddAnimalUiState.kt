package com.gromber05.peco.ui.screens.admin

import com.gromber05.peco.model.AdoptionState

/**
 * Representa el estado atómico de la pantalla [AdminAddAnimalScreen].
 * Contiene toda la información necesaria para renderizar el formulario y gestionar
 * el proceso de persistencia de un nuevo animal.
 *
 * @property name Nombre del animal introducido en el formulario.
 * @property species Especie del animal (ej: "Perro", "Gato").
 * @property dob Fecha de nacimiento (Date of Birth) en formato de texto.
 * @property photo URL de la fotografía (si ya existiera o fuera remota).
 * @property latitude Coordenada de latitud en formato String para facilitar su edición en campos de texto.
 * @property longitude Coordenada de longitud en formato String.
 * @property photoBytes El contenido binario de la imagen seleccionada localmente, listo para subirse a Storage.
 * @property photoUri La URI local de la imagen para mostrar la previsualización inmediata en la UI.
 * @property adoptionState El estado actual de adopción seleccionado mediante el dropdown.
 * @property isSaving Indicador de carga (loading) que bloquea la UI mientras se realiza la operación en red.
 */
data class AdminAddAnimalUiState(
    val name: String = "",
    val species: String = "",
    val dob: String = "",
    val photo: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val photoBytes: ByteArray? = null,
    val photoUri: String? = null,
    val adoptionState: AdoptionState = AdoptionState.AVAILABLE,
    val isSaving: Boolean = false
)