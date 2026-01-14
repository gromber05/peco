package com.gromber05.peco.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gromber05.peco.ui.screens.login.LoginViewModel

@Composable
fun HomeScreen(
    viewModel : LoginViewModel = hiltViewModel(),
    email: String = ""
) {
    val state by viewModel.uiState.collectAsState()
    var selectPage by rememberSaveable() { mutableStateOf(0) }

    //TODO. Tienes que hacer una función que cuando se inicie el menú home, cargue el usuario desde el email para que se pueda actualizar también desde aquí

    Scaffold(
        topBar = { MyTopAppBar(name = state.user) }
    ) { innerPadding ->
        when (selectPage) {
            0 -> {
                SettingsView(Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun HomeView(modifier: Modifier = Modifier) {

}

@Composable
fun SettingsView(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
    ) {

    }
}

@Composable
fun MyTopAppBar(name: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(
            bottomStart = 30.dp,
            bottomEnd = 30.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¡Hola, ${name}!",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp
            )
        }
    }
}

@Preview
@Composable
fun Preview_MyTopAppBar() {
    MyTopAppBar(
        name = "Gonzalo"
    )
}

@Preview
@Composable
fun Preview_HomeScreen() {
    HomeScreen()
}