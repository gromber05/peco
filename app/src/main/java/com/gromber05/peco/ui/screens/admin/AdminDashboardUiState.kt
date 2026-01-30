package com.gromber05.peco.ui.screens.admin

import com.gromber05.peco.model.data.LabelCount

data class AdminDashboardUiState(
    val totalAnimals: Int = 0,
    val available: Int = 0,
    val adopted: Int = 0,
    val pending: Int = 0,
    val likes: Int = 0,
    val dislikes: Int = 0,
    val bySpecies: List<LabelCount> = emptyList(),
    val topLikedSpecies: List<LabelCount> = emptyList(),
    val selectedSpeciesFilter: String? = null,
    val error: String? = null
)
