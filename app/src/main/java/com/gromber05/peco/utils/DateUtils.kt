package com.gromber05.peco.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Formato de entrada esperado para fechas recibidas como texto.
 *
 * Se utiliza el patrón `yyyy-MM-dd`, habitual en APIs REST.
 * La localización se fija a español (España) para coherencia
 * con el resto de conversiones de fecha.
 */
private val INPUT_FMT = SimpleDateFormat(
    "yyyy-MM-dd",
    Locale("es", "ES")
)

/**
 * Convierte una fecha en formato `String` (`yyyy-MM-dd`) a un objeto [Date].
 *
 * Si el texto no cumple el formato esperado o no puede parsearse,
 * la función devuelve `null` en lugar de lanzar una excepción.
 *
 * @param date Fecha en formato `yyyy-MM-dd`.
 * @return Objeto [Date] resultante o `null` si el parseo falla.
 */
fun parseDateApi(date: String): Date? =
    runCatching { INPUT_FMT.parse(date) }.getOrNull()

/**
 * Convierte un objeto [Date] a un texto legible en español.
 *
 * El formato generado es:
 * `d de MMMM de yyyy`
 *
 * Ejemplo:
 * - `2024-01-15` → `15 de enero de 2024`
 *
 * Esta función es compatible con API 23.
 *
 * @param date Fecha a convertir.
 * @return Cadena de texto con la fecha formateada en español.
 */
fun dateToSpokenTextApi23(date: Date): String {
    val spokenFormat = SimpleDateFormat(
        "d 'de' MMMM 'de' yyyy",
        Locale("es", "ES")
    )
    return spokenFormat.format(date)
}

/**
 * Convierte una fecha en formato `String` (`yyyy-MM-dd`) a texto legible en español.
 *
 * Flujo interno:
 * - Intenta parsear la fecha con [parseDateApi].
 * - Si el parseo es correcto, la transforma a texto usando [dateToSpokenTextApi23].
 * - Si falla, devuelve una cadena por defecto.
 *
 * @param date Fecha en formato `yyyy-MM-dd`.
 * @return Fecha convertida a texto legible o `"fecha desconocida"` si no se puede procesar.
 */
fun fechaATexto(date: String): String {
    val fechaParse = parseDateApi(date)
    return fechaParse?.let { dateToSpokenTextApi23(it) } ?: "fecha desconocida"
}
