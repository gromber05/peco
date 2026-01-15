package com.gromber05.peco.utils.converters

import androidx.room.TypeConverter
import com.gromber05.peco.data.local.swipe.SwipeAction

class SwipeConverters {
    @TypeConverter
    fun fromSwipeAction(value: SwipeAction): String = value.name

    @TypeConverter
    fun toSwipeAction(value: String): SwipeAction = SwipeAction.valueOf(value)
}
