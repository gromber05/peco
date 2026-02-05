package com.gromber05.peco.ui.screens.home

import com.gromber05.peco.model.data.Animal
import com.gromber05.peco.model.user.UserRole

/**
 * Estado de UI para la pantalla principal (Home).
 *
 * Contiene toda la información necesaria para renderizar correctamente
 * la pantalla de inicio de la aplicación, incluyendo:
 * - Datos del usuario autenticado.
 * - Listados de animales y estado de interacciones (likes/swipes).
 * - Información de sesión, carga y errores.
 *
 * Este estado es producido por [HomeViewModel] y consumido por Jetpack Compose,
 * permitiendo una UI reactiva y desacoplada de la lógica de negocio.
 */
data class HomeUiState(

    /**
     * Indica si la pantalla está cargando información.
     *
     * Mientras es `true`, la UI puede mostrar indicadores de progreso.
     */
    val isLoading: Boolean = true,

    /**
     * Indica si el usuario está autenticado.
     *
     * Se usa como apoyo para decisiones de navegación o UI condicional.
     */
    val isLogged: Boolean = false,

    /**
     * UID del usuario autenticado.
     *
     * Será `null` si el usuario no está logueado o el estado aún no se ha resuelto.
     */
    val userUid: String? = null,

    /**
     * Nombre visible del usuario.
     */
    val username: String = "",

    /**
     * Correo electrónico del usuario.
     */
    val email: String = "",

    /**
     * Rol del usuario dentro de la aplicación.
     *
     * Determina permisos y visibilidad de secciones como el panel de administración.
     */
    val userRole: UserRole = UserRole.USER,

    /**
     * URL de la foto de perfil del usuario.
     */
    val photo: String? = null,

    /**
     * Foto de perfil en formato ByteArray.
     *
     * Se utiliza normalmente cuando el usuario selecciona una imagen
     * antes de subirla a almacenamiento remoto.
     */
    val photoBytes: ByteArray? = null,

    /**
     * URI local de la foto de perfil seleccionada.
     *
     * Se usa para previsualización en UI antes de la subida definitiva.
     */
    val photoUri: String? = null,

    /**
     * Lista completa de animales disponibles para mostrar en Home.
     */
    val animalList: List<Animal> = emptyList(),

    /**
     * Lista de animales preparada como "deck" (por ejemplo para swipes).
     *
     * Puede ser una sublista o versión ordenada de [animalList].
     */
    val deck: List<Animal> = emptyList(),

    /**
     * Conjunto de IDs de animales que ya han sido swipeados por el usuario.
     *
     * Se utiliza para ocultarlos del listado visible.
     */
    val swipedIds: Set<String> = emptySet(),

    /**
     * Conjunto de IDs de animales marcados como favoritos (LIKE).
     */
    val likedIds: Set<String> = emptySet(),

    /**
     * Mensaje de error a mostrar en la UI.
     *
     * Si es `null`, no hay errores activos.
     */
    val error: String? = null
)