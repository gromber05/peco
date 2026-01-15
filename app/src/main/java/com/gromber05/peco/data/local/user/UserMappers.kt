package com.gromber05.peco.data.local.user

import com.gromber05.peco.model.data.User
import kotlin.String

fun User.toUserEntity(): UserEntity = UserEntity(
    id = id,
    username = username,
    password = password,
    email = email,
    photo = photo
)

fun UserEntity.toUser(): User = User(
    id = id,
    username = username,
    password = password,
    email = email,
    photo = photo
)