package com.gromber05.peco.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gromber05.peco.data.repository.AdminStatsRepository
import com.gromber05.peco.model.data.LabelCount
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repo: AdminStatsRepository
) : ViewModel() {

    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(AdminDashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeStats()
    }

    fun setSpeciesFilter(species: String?) {
        _uiState.update { it.copy(selectedSpeciesFilter = species) }
    }

    private data class AnimalCounts(
        val total: Int,
        val available: Int,
        val adopted: Int,
        val pending: Int
    )

    private data class SwipeStats(
        val likes: Int,
        val dislikes: Int,
        val bySpecies: List<LabelCount>,
        val topLiked: List<LabelCount>
    )

    private fun observeStats() {
        viewModelScope.launch {
            val countsFlow = combine(
                repo.totalAnimals().distinctUntilChanged(),
                repo.availableAnimals().distinctUntilChanged(),
                repo.adoptedAnimals().distinctUntilChanged(),
                repo.pendingAnimals().distinctUntilChanged(),
            ) { total, available, adopted, pending ->
                AnimalCounts(total, available, adopted, pending)
            }

            val statsFlow = combine(
                repo.likes().distinctUntilChanged(),
                repo.dislikes().distinctUntilChanged(),
                repo.animalsBySpecies().distinctUntilChanged(),
                repo.topLikedSpecies().distinctUntilChanged(),
            ) { likes, dislikes, bySpecies, topLiked ->
                SwipeStats(likes, dislikes, bySpecies, topLiked)
            }

            combine(countsFlow, statsFlow) { counts, stats ->
                val currentFilter = _uiState.value.selectedSpeciesFilter

                AdminDashboardUiState(
                    totalAnimals = counts.total,
                    available = counts.available,
                    adopted = counts.adopted,
                    pending = counts.pending,
                    likes = stats.likes,
                    dislikes = stats.dislikes,
                    bySpecies = stats.bySpecies,
                    topLikedSpecies = stats.topLiked,
                    selectedSpeciesFilter = currentFilter,
                    error = null
                )
            }
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message ?: "Permisos insuficientes o error de red")
                    }
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }
}
