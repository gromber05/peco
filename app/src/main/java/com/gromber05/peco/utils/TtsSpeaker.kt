package com.gromber05.peco.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Utilidad para síntesis de voz (Text-to-Speech) en español.
 *
 * Esta clase encapsula el uso de [TextToSpeech] para simplificar:
 * - La inicialización del motor TTS.
 * - La configuración del idioma.
 * - La reproducción de texto por voz.
 * - La liberación correcta de recursos.
 *
 * Está pensada para usarse desde ViewModels o capas de UI (por ejemplo,
 * para accesibilidad o lectura de información al usuario).
 *
 * @param context Contexto Android necesario para inicializar el motor TTS.
 */
class TtsSpeaker(context: Context) : TextToSpeech.OnInitListener {

    /**
     * Instancia interna del motor Text-to-Speech.
     */
    private val tts = TextToSpeech(context, this)

    /**
     * Indica si el motor TTS está listo para reproducir voz.
     */
    private var ready = false

    /**
     * Callback invocado cuando el motor Text-to-Speech ha terminado de inicializarse.
     *
     * Si la inicialización es correcta, se marca el motor como listo y se
     * establece el idioma a español (España).
     *
     * @param status Estado de inicialización devuelto por [TextToSpeech].
     */
    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        tts.language = Locale("es", "ES")
    }

    /**
     * Reproduce un texto mediante síntesis de voz.
     *
     * Si el motor aún no está listo, la función no realiza ninguna acción.
     * El texto se reproduce limpiando cualquier cola previa (`QUEUE_FLUSH`).
     *
     * @param text Texto a reproducir por voz.
     */
    fun speak(text: String) {
        if (!ready) return
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance")
    }

    /**
     * Libera los recursos asociados al motor Text-to-Speech.
     *
     * Debe llamarse cuando el componente que usa esta clase
     * deja de ser necesario (por ejemplo, en `onCleared()` de un ViewModel
     * o en el ciclo de vida de una pantalla).
     */
    fun release() = tts.shutdown()
}
