package com.gromber05.peco.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gromber05.peco.model.AdoptionState
import com.gromber05.peco.model.Animal

@Composable
fun AnimalCard(
    modifier: Modifier = Modifier,
    animal: Animal,
    onDetails: () -> Unit
) {
    val isFavorite by rememberSaveable{ mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onDetails)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (animal.photo != null) {
                            AsyncImage(
                                model = animal.photo,
                                contentDescription = "Foto de ${animal.name}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Animal sin imagen",
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column {
                            AnimalName("Nombre", animal.name)
                            Spacer(modifier = Modifier.padding(2.dp))
                            AnimalName("Especie", animal.species)
                        }

                        Spacer(modifier = Modifier.padding(4.dp))

                        Column {
                            AnimalName("Nacimiento", animal.dob)
                            Spacer(modifier = Modifier.padding(2.dp))
                            AnimalName("Estado", animal.adoptionState.value)
                        }
                    }
                }

                IconButton(
                    onClick = { /* TODO */ }
                ) {
                    Crossfade(targetState = isFavorite, label = "HeartAnimation") { liked ->
                        Icon(
                            imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (liked) Color.Red else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimalName(value: String, name: String) {
    return Column() {
        Text(
            text = value,
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = " $name",
            fontSize = 16.sp,
        )
    }
}


@Preview
@Composable
fun AnimalCardPreview() {
   Column (
       horizontalAlignment = Alignment.CenterHorizontally
   ){
       AnimalCard(
           modifier = Modifier.fillMaxWidth(),
           animal = Animal(
               name = "Mario",
               species = "Perro",
               photo = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRcci_BP3wtsbh1-gFdV4FXfyMWkdw1GyO-0-tvNLGhRGqc1YL8tuZWS05CdGtePNgYc5ESKo7BmbEaDywuWbSDJmwA7v6t9wuVIr79Cw&s=10",
               dob = "29/11",
               adoptionState = AdoptionState.AVAILABLE,
           ),
           onDetails = {}
       )
   }
}


