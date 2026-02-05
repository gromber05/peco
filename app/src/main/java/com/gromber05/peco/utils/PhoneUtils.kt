package com.gromber05.peco.utils

/**
 * Normaliza un número de teléfono eliminando caracteres no válidos.
 *
 * Esta función:
 * - Elimina espacios en blanco al inicio y al final.
 * - Conserva únicamente dígitos (`0-9`) y el carácter `+`.
 *
 * Es útil para unificar el formato del teléfono antes de validarlo
 * o almacenarlo (por ejemplo, antes de enviarlo a backend o base de datos).
 *
 * Ejemplos:
 * - `"  +34 612 34 56 78 "` → `"+34612345678"`
 * - `"612-34-56-78"` → `"612345678"`
 *
 * @param phone Número de teléfono introducido por el usuario.
 * @return Número de teléfono normalizado.
 */
fun normalizePhone(phone: String): String {
    return phone
        .trim()
        .filter { it.isDigit() || it == '+' }
}
