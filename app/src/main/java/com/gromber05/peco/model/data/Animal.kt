package com.gromber05.peco.model.data

import com.gromber05.peco.model.AdoptionState

/**
 * Modelo de datos que representa a un animal dentro de la plataforma.
 * Esta clase se utiliza para mapear la información proveniente de Firestore y
 * para gestionar la lógica de visualización en la interfaz de usuario.
 *
 * @property uid Identificador único del animal (ID del documento en Firestore).
 * @property name Nombre del animal.
 * @property species Especie a la que pertenece (ej: Perro, Gato, Ave).
 * @property photo URL de la imagen del animal almacenada en Firebase Storage. Puede ser null si no tiene foto.
 * @property dob Fecha de nacimiento (Date of Birth) representada como String.
 * @property latitude Coordenada de latitud de la ubicación donde se encuentra el animal.
 * @property longitude Coordenada de longitud de la ubicación donde se encuentra el animal.
 * @property adoptionState Estado actual del proceso de adopción (definido en [AdoptionState]).
 * @property volunteerId ID del usuario (voluntario) que gestiona o registró a este animal.
 */
data class Animal(
    val uid: String,
    val name: String,
    val species: String,
    val photo: String?,
    val dob: String,
    val latitude: Double,
    val longitude: Double,
    val adoptionState: AdoptionState,
    val volunteerId: String
)