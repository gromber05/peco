package com.gromber05.peco.ui.navigation

/**
 * Define la estructura de navegación de la aplicación.
 * Utiliza una [sealed class] para asegurar que el grafo de navegación solo contenga
 * destinos predefinidos, facilitando el mantenimiento y la escalabilidad.
 *
 * @property route La cadena de texto que identifica de forma única la ruta en el NavHost.
 */
sealed class AppNavigation(val route: String) {

    /** Pantalla principal de descubrimiento (Deck de animales). */
    object MainScreen: AppNavigation(route = "home_screen")

    /** Pantalla de inicio de sesión. */
    object LoginScreen: AppNavigation(route = "login_screen")

    /** Pantalla donde el voluntario puede ver sus animales registrados. */
    object MyAnimalsScreen: AppNavigation(route = "myanimals")

    /** * Pantalla de detalles de un animal específico.
     * Incluye un argumento dinámico {animalId} en la ruta.
     */
    object DetailScreen: AppNavigation(route = "detail/{animalId}") {
        /** Construye la ruta final con el identificador del animal proporcionado. */
        fun createRoute(animalId: String) = "detail/$animalId"
    }

    /** Pantalla para el registro de nuevos usuarios. */
    object RegisterScreen: AppNavigation(route = "register_screen")

    /** Pantalla para solicitar la recuperación de contraseña mediante email. */
    object ForgotPassword : AppNavigation("forgot_password")

    /** Pantalla para que el usuario modifique su nombre o foto de perfil. */
    object EditProfile: AppNavigation(route = "edit_profile")

    /** Pantalla dedicada exclusivamente al cambio de contraseña. */
    object ChangePassword: AppNavigation(route = "change_password")

    /** * Pantalla de control (Gate).
     * Suele usarse para decidir si redirigir al Login o a la MainScreen al arrancar la app.
     */
    object Gate: AppNavigation(route = "gate")

    /** Pantalla con el formulario para añadir un nuevo animal al sistema. */
    object AddAnimalScreen: AppNavigation(route = "addanimalscreen")
}