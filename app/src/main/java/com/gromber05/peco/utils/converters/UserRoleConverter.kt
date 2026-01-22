package com.gromber05.peco.utils.converters

import androidx.room.TypeConverter
import com.gromber05.peco.model.user.UserRole

class UserRoleConverter {
    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.USER)

    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name
}