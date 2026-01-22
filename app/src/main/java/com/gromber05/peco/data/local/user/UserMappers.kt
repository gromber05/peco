package com.gromber05.peco.data.local.user

import com.gromber05.peco.model.user.User
import kotlin.Boolean

fun User.toUserEntity(): UserEntity = UserEntity(
    id = id,
    username = username,
    password = password,
    email = email,
    photo = photo,
    role = role
)

fun UserEntity.toUser(): User = User(
    id = id,
    username = username,
    password = password,
    email = email,
    photo = photo,
    role = role
)