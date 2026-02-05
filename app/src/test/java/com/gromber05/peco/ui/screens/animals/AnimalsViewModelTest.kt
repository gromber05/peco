@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.gromber05.peco.ui.screens.animals

import app.cash.turbine.test
import com.gromber05.peco.MainDispatcherRule
import com.gromber05.peco.data.repository.AnimalRepository
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.data.repository.SwipeRepository
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.testAnimal
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AnimalsViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @Test
    fun `filter false devuelve solo favoritos`() = runTest {
        val animalRepo = mockk<AnimalRepository>()
        val swipeRepo = mockk<SwipeRepository>()
        val authRepo = mockk<AuthRepository>()

        val animals = listOf(
            testAnimal(uid = "a1", volunteerId = "v1"),
            testAnimal(uid = "a2", volunteerId = "v2", adoptionState = AdoptionState.ADOPTED)
        )

        every { animalRepo.observeAnimals() } returns flowOf(animals)
        every { swipeRepo.observeLikedIds("u1") } returns flowOf(setOf("a2"))
        every { authRepo.currentUidFlow() } returns flowOf("u1")

        val vm = AnimalsViewModel(
            animalRepository = animalRepo,
            swipeRepository = swipeRepo,
            authRepository = authRepo
        )

        vm.setFilter(false)

        vm.uiState.test {
            val s0 = awaitItem()
            val s1 = awaitItem()
            val state = if (s1.animals.isEmpty()) awaitItem() else s1

            assertEquals(listOf("a2"), state.animals.map { it.uid })
        }


    }
}
