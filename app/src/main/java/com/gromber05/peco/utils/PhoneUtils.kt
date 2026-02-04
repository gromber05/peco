package com.gromber05.peco.utils

fun normalizePhone(phone: String): String {
    return phone
        .trim()
        .filter { it.isDigit() || it == '+' }
}