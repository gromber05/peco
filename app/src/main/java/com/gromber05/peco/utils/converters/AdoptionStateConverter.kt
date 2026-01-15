package com.gromber05.peco.utils.converters

import androidx.room.TypeConverter
import com.gromber05.peco.model.AdoptionState

class AdoptionStateConverter {

    @TypeConverter
    fun fromAdoptionState(state: AdoptionState): String =
        state.name

    @TypeConverter
    fun toAdoptionState(value: String): AdoptionState =
        AdoptionState.valueOf(value)
}
