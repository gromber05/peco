package com.gromber05.peco.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AdminStatsRepository
import com.gromber05.peco.model.data.LabelCount
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val totalAnimals: Int = 0,
    val available: Int = 0,
    val adopted: Int = 0,
    val pending: Int = 0,
    val likes: Int = 0,
    val dislikes: Int = 0,
    val bySpecies: List<LabelCount> = emptyList(),
    val topLikedSpecies: List<LabelCount> = emptyList(),
    val selectedSpeciesFilter: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repo: AdminStatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        observeStats()
    }

    fun setSpeciesFilter(species: String?) {
        _uiState.update { it.copy(selectedSpeciesFilter = species) }
    }

    private fun observeStats() {
        viewModelScope.launch {
            val a = combine(
                repo.totalAnimals(),
                repo.availableAnimals(),
                repo.adoptedAnimals(),
                repo.pendingAnimals()
            ) { total, available, adopted, pending ->
                arrayOf(total, available, adopted, pending)
            }

            val b = combine(
                repo.likes(),
                repo.dislikes(),
                repo.animalsBySpecies(),
                repo.topLikedSpecies()
            ) { likes, dislikes, bySpecies, topLiked ->
                arrayOf(likes, dislikes, bySpecies, topLiked)
            }

            combine(a, b) { left, right ->
                val total = left[0]
                val available = left[1]
                val adopted = left[2]
                val pending = left[3]

                val likes = right[0] as Int
                val dislikes = right[1] as Int
                val bySpecies = right[2] as List<LabelCount>
                val topLiked = right[3] as List<LabelCount>

                AdminDashboardUiState(
                    totalAnimals = total,
                    available = available,
                    adopted = adopted,
                    pending = pending,
                    likes = likes,
                    dislikes = dislikes,
                    bySpecies = bySpecies,
                    topLikedSpecies = topLiked,
                    selectedSpeciesFilter = _uiState.value.selectedSpeciesFilter
                )
            }.collect { _uiState.value = it }
        }
    }

}
