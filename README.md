# 🐾 Peco - Gestión de Protectora de Animales

**Peco** es una aplicación móvil nativa para Android diseñada para optimizar las operaciones diarias de una protectora de animales. Su objetivo es conectar a adoptantes con mascotas que buscan hogar y facilitar la gestión técnica e interna por parte de los administradores.

---

## 📱 Capturas de Pantalla

| Login | Home (Usuario) | Panel Admin |
| --- | --- | --- |
| 🖼️ | 🖼️ | 🖼️ |

---

## 🚀 Tecnologías y Arquitectura

El proyecto sigue las **Modern Android Development (MAD)** guidelines de Google, utilizando un stack tecnológico de vanguardia:

* **Lenguaje:** [Kotlin 2.1.0+](https://kotlinlang.org/) 
* **Interfaz de Usuario (UI):** [Jetpack Compose](https://developer.android.com/jetpack/compose) con **Material Design 3**.
* **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture orientada a capas.
* **Inyección de Dependencias:** [Hilt](https://dagger.dev/hilt/) (Dagger) para una gestión de dependencias desacoplada.
* **Navegación:** Type-safe Navigation con Jetpack Compose.
* **Persistencia de Datos:** [Room Database](https://developer.android.com/training/data-storage/room)
* **Carga de Imágenes:** [Coil](https://coil-kt.github.io/coil/compose/) (Image loading asíncrono).
* **Asincronía:** Kotlin Coroutines & Flow.
* **Gráficos:** [Vico](https://github.com/patrykandpatrick/vico) para la visualización de datos estadísticos.

---

## ✨ Funcionalidades Principales

### 👤 Para Usuarios
* **Autenticación:** Sistema de Login y Registro seguro.
* **Exploración:** Feed dinámico de animales con estados actualizados en tiempo real.
* **Interacción:** Sistema de gestos (Swipe) para interactuar con las fichas de animales.
* **Perfil:** Gestión de datos de usuario y preferencias.

### 🛡️ Para Administradores
* **Gestión de Inventario:** CRUD completo (Crear, Leer, Actualizar, Borrar) de animales.
* **Geolocalización:** Registro de coordenadas GPS de rescate mediante mapas/coordenadas.
* **Dashboard Estadístico:** Visualización mediante gráficas del flujo de adopciones y animales rescatados.
* **Roles:** Control de acceso basado en roles gestionado por `SessionRepository`.

---

## 📁 Estructura del Proyecto

El proyecto sigue una arquitectura **MVVM + separación por capas**, adaptada a Jetpack Compose y organizada por responsabilidad:

> La aplicación mantiene una separación clara entre la capa de datos, dominio y presentación.
> La UI no accede directamente a la base de datos, sino a través de repositorios, garantizando escalabilidad, testabilidad y mantenibilidad.


```text    
com.gromber05.peco
├── data                        # Capa de datos: acceso y persistencia
│   ├── di                      # Módulos de Hilt (inyección de dependencias)
│   ├── local                   # Persistencia local (Room)
│   │   ├── animal              # Entidades, DAO y lógica de mascotas
│   │   ├── swipe               # Lógica relacionada con interacciones/swipe
│   │   └── user                # Persistencia de usuarios y cuentas
│   ├── repository              # Implementaciones de repositorios (SSOT)
│   └── session                 # Gestión de sesión y DataStore
│
├── model                       # Modelos de dominio y estados de UI
│
├── ui                          # Capa de presentación (Jetpack Compose)
│   ├── components              # Componentes reutilizables (Cards, Buttons, etc.)
│   ├── navigation              # Rutas y grafos de navegación
│   ├── screens                 # Pantallas (Login, Home, Admin, Profile, etc.)
│   └── theme                   # Design System (Material 3, colores, tipografía)
│
└── utils                       # Helpers, type converters y utilidades comunes
```

---

## 🛠️ Requisitos e Instalación

1. **Clonar el repositorio:**
```bash
git clone https://git.gonzaloromerobernal.es/IESRafaelAlberti/peco.git
```

2. **Abrir en Android Studio:**
* **Android Studio:** (2024.2.1) o superior.
* **JDK:** Java 17 o superior.
* **Gradle:** 8.x con soporte para Kotlin 2.0.


3. **Sincronizar:**
* Espera a que Gradle descargue las dependencias e indexe el proyecto.


4. **Ejecutar:**
* Conecta un dispositivo físico o inicia un emulador y pulsa `Run`.

---

## 🔧 Solución de Problemas Comunes

### Error: `Using 'jvmTarget: String' is an error`

Si al compilar con **Kotlin 2.0+** recibes este error, es porque la sintaxis de Gradle ha cambiado.

Ve a tu archivo `app/build.gradle.kts` y actualiza el bloque de Kotlin así:

```kotlin
// ❌ ANTES (Deprecado en Kotlin 2.x)
/*
kotlinOptions {
    jvmTarget = "11"
}
*/

// ✅ AHORA (Correcto)
compilerOptions {
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
}

```
