package com.gromber05.peco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gromber05.peco.app.PecoApp
import com.gromber05.peco.data.repository.SettingsRepository
import com.gromber05.peco.data.session.AppPreferences
import com.gromber05.peco.ui.AppViewModel
import com.gromber05.peco.ui.theme.PecoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = hiltViewModel()
            val darkMode by appViewModel.darkMode.collectAsState(initial = false)

            PecoTheme(darkTheme = darkMode) {
                PecoApp(
                    isDark = darkMode
                )
            }
        }
    }
}




