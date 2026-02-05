package com.gromber05.peco.model

/**
 * Define las acciones posibles que un usuario puede realizar al interactuar con la tarjeta de un animal.
 * Estas acciones determinan si un animal se añade a la lista de interés o se descarta.
 */
enum class SwipeAction {
    /** * Indica que el usuario tiene interés en el animal.
     * Esta acción suele guardar el ID del animal en la colección de "favoritos" o "likes" del usuario.
     */
    LIKE,

    /** * Indica que el usuario ha decidido descartar al animal.
     * Generalmente se utiliza para filtrar la lista y no volver a mostrar este animal al mismo usuario.
     */
    DISLIKE
}