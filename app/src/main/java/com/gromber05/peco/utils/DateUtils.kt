package com.gromber05.peco.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val INPUT_FMT = SimpleDateFormat(
    "yyyy-MM-dd",
    Locale("es", "ES")
)

fun parseDateApi(date: String): Date? =
    runCatching { INPUT_FMT.parse(date) }.getOrNull()

fun dateToSpokenTextApi23(date: Date): String {
    val spokenFormat = SimpleDateFormat(
        "d 'de' MMMM 'de' yyyy",
        Locale("es", "ES")
    )
    return spokenFormat.format(date)
}

fun fechaATexto(date: String): String {
    val fechaParse = parseDateApi(date)
    return fechaParse?.let { dateToSpokenTextApi23(it) } ?: "fecha desconocida"
}
