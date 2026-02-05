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

/**
 * ViewModel del panel de administración (dashboard).
 *
 * Responsabilidades:
 * - Suscribirse a los flujos de estadísticas proporcionados por [AdminStatsRepository].
 * - Combinar múltiples flujos (animales y swipes) en un único estado de UI.
 * - Exponer el estado como [StateFlow] mediante [uiState].
 * - Gestionar filtros de UI (por ejemplo, especie seleccionada).
 * - Capturar errores (permisos / red) y reflejarlos en el estado.
 *
 * Implementación:
 * - Usa [combine] para agrupar datos de varios Flows en estructuras intermedias.
 * - Usa [distinctUntilChanged] para evitar actualizaciones redundantes.
 * - Lanza la observación en [viewModelScope] para respetar el ciclo de vida del ViewModel.
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    /** Repositorio que expone estadísticas agregadas desde Firestore. */
    private val repo: AdminStatsRepository
) : ViewModel() {

    /**
     * Estado interno mutable del dashboard.
     * Se expone de manera inmutable con [uiState] para ser consumido por Compose.
     */
    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(AdminDashboardUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Inicializa la observación de estadísticas al crear el ViewModel.
     *
     * Nota:
     * - También existe el método público [observeStats], que se llama aquí por defecto.
     * - Si la UI lo llama de nuevo, podría duplicar colecciones si no se controla.
     */
    init {
        observeStats()
    }

    /**
     * Establece (o limpia) el filtro de especie seleccionado en el dashboard.
     *
     * Este filtro se guarda en el estado para que la UI pueda reflejarlo.
     * (Actualmente el filtro se conserva en el estado, pero no se aplica al contenido;
     * podría aplicarse en UI o en el propio ViewModel al construir el estado).
     *
     * @param species Especie seleccionada, o `null` para no filtrar.
     */
    fun setSpeciesFilter(species: String?) {
        _uiState.update { it.copy(selectedSpeciesFilter = species) }
    }

    /**
     * Estructura interna para agrupar contadores de animales.
     *
     * Se usa como resultado intermedio del `combine` de flujos
     * relacionados con animales.
     */
    private data class AnimalCounts(
        val total: Int,
        val available: Int,
        val adopted: Int,
        val pending: Int
    )

    /**
     * Estructura interna para agrupar estadísticas relacionadas con swipes y especies.
     *
     * Se usa como resultado intermedio del `combine` de flujos
     * relacionados con interacción de usuarios.
     */
    private data class SwipeStats(
        val likes: Int,
        val dislikes: Int,
        val bySpecies: List<LabelCount>,
        val topLiked: List<LabelCount>
    )

    /**
     * Inicia la observación de estadísticas en tiempo real.
     *
     * Combina dos grupos principales de datos:
     * 1) Contadores de animales ([AnimalCounts])
     * 2) Estadísticas de swipes y especies ([SwipeStats])
     *
     * Luego combina ambos en un único [AdminDashboardUiState] que consume la UI.
     *
     * Manejo de errores:
     * - Si ocurre una excepción (por ejemplo, permisos de Firestore o error de red),
     *   se captura con [catch] y se guarda el mensaje en `error`.
     */
    fun observeStats() {
        viewModelScope.launch {
            /**
             * Flow que agrupa métricas relacionadas con animales.
             *
             * `distinctUntilChanged()` reduce emisiones repetidas cuando el valor no cambia.
             */
            val countsFlow = combine(
                repo.totalAnimals().distinctUntilChanged(),
                repo.availableAnimals().distinctUntilChanged(),
                repo.adoptedAnimals().distinctUntilChanged(),
                repo.pendingAnimals().distinctUntilChanged(),
            ) { total, available, adopted, pending ->
                AnimalCounts(total, available, adopted, pending)
            }

            /**
             * Flow que agrupa métricas relacionadas con swipes y distribución por especie.
             */
            val statsFlow = combine(
                repo.likes().distinctUntilChanged(),
                repo.dislikes().distinctUntilChanged(),
                repo.animalsBySpecies().distinctUntilChanged(),
                repo.topLikedSpecies().distinctUntilChanged(),
            ) { likes, dislikes, bySpecies, topLiked ->
                SwipeStats(likes, dislikes, bySpecies, topLiked)
            }

            /**
             * Flow final de estado: combina conteos y estadísticas para construir el UIState.
             * Se conserva el filtro actual de especie desde el estado existente.
             */
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
                    // Captura errores típicos: permisos insuficientes o fallos de red.
                    _uiState.update {
                        it.copy(error = e.message ?: "Permisos insuficientes o error de red")
                    }
                }
                .collect { newState ->
                    // Publica el nuevo estado para que Compose recomponda.
                    _uiState.value = newState
                }
        }
    }
}
