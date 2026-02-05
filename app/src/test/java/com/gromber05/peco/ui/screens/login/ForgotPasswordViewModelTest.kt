@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.gromber05.peco.ui.screens.login

import app.cash.turbine.test
import com.gromber05.peco.MainDispatcherRule
import com.gromber05.peco.data.repository.AuthRepository
import com.gromber05.peco.ui.screens.forgotpassword.ForgotPasswordViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ForgotPasswordViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @Test
    fun `sendReset marca success cuando todo va bien`() = runTest {
        val authRepo = mockk<AuthRepository>()
        coEvery { authRepo.sendPasswordResetEmail("test@mail.com") } returns Unit

        val vm = ForgotPasswordViewModel(authRepo)
        vm.onEmailChange("test@mail.com")
        vm.sendReset()

        vm.uiState.test {
            awaitItem()
            awaitItem()
            val successState = awaitItem()

            assertTrue(successState.success)
        }
    }

    @Test
    fun `sendReset muestra error si falla el repo`() = runTest {
        val authRepo = mockk<AuthRepository>()
        coEvery { authRepo.sendPasswordResetEmail(any()) } throws RuntimeException("Firebase error")

        val vm = ForgotPasswordViewModel(authRepo)
        vm.onEmailChange("test@mail.com")
        vm.sendReset()

        vm.uiState.test {
            awaitItem()
            awaitItem()
            val errorState = awaitItem()

            assertTrue(errorState.error?.contains("Firebase") == true)
        }
    }
}
