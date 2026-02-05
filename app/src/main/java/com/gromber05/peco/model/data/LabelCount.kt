package com.gromber05.peco.model.data

/**
 * Modelo de datos genérico diseñado para representar estadísticas y conteos.
 * Se utiliza comúnmente en la capa de administración para agrupar elementos por categorías.
 *
 * @property label La etiqueta o nombre de la categoría (ej: "Perros", "Gatos", "Likes").
 * @property count El valor numérico o cantidad asociada a dicha etiqueta.
 */
data class LabelCount(
    val label: String,
    val count: Int
)