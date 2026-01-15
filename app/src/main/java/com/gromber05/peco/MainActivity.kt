package com.gromber05.peco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gromber05.peco.app.PecoApp
import com.gromber05.peco.ui.theme.PecoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            PecoTheme(darkTheme = isDarkMode) {
                PecoApp(
                    onToggleTheme = { isDarkMode = !isDarkMode},
                    isDark = isDarkMode
                )
            }
        }
    }
}




