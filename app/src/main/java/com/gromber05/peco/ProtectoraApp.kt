package com.gromber05.peco

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase principal de la aplicación.
 *
 * Inicializa Hilt como sistema de inyección de dependencias mediante
 * la anotación [@HiltAndroidApp], permitiendo su uso en toda la app.
 */
@HiltAndroidApp
class ProtectoraApp : Application()
