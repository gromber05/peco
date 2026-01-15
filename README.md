# 🐾 Peco - Gestión de Protectora de Animales

**Peco** es una aplicación móvil nativa para Android diseñada para optimizar las operaciones diarias de una protectora de animales. Su objetivo es conectar a adoptantes con mascotas que buscan hogar y facilitar la gestión interna por parte de los administradores.

---

## 📱 Capturas de Pantalla

| Login | Home (Usuario) | Detalle Mascota |
| --- | --- | --- |
| 🖼️ | 🖼️ | 🖼️ |

---

## 🚀 Tecnologías y Arquitectura

El proyecto sigue las **Modern Android Development (MAD)** guidelines de Google:

* **Lenguaje:** [Kotlin 2.x](https://kotlinlang.org/)
* **Interfaz de Usuario (UI):** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3).
* **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture (por capas).
* **Inyección de Dependencias:** [Hilt](https://dagger.dev/hilt/) (Dagger).
* **Navegación:** Jetpack Compose Navigation.
* **Persistencia de Datos:** [Room Database](https://developer.android.com/training/data-storage/room).
* **Carga de Imágenes:** [Coil](https://coil-kt.github.io/coil/compose/).
* **Asincronía:** Kotlin Coroutines & Flow.
* **Gráficos:** Vico (para estadísticas).

---

## ✨ Funcionalidades Principales

### 👤 Para Usuarios

* **Registro e Inicio de Sesión:** Acceso seguro a la plataforma.
* **Exploración:** Visualización de lista de animales en adopción con filtros.
* **Detalle:** Ficha completa de cada mascota (fotos, descripción, edad, estado).

### 🛡️ Para Administradores

* **Gestión de Inventario:** Alta, baja y modificación de fichas de animales.
* **Roles:** Permisos especiales detectados automáticamente tras el login.
* **Estadísticas:** (En desarrollo) Visualización de adopciones mensuales.

---

## 📁 Estructura del Proyecto

El código está organizado siguiendo una estructura modular por capas (features/screens):

```text
com.gromber05.peco
├── data                # Capa de Datos (Room, Repositorios)
│   ├── dao
│   ├── entity
│   └── repository
├── di                  # Inyección de Dependencias (Hilt Modules)
├── domain              # Modelos de dominio y UseCases (si aplica)
├── navigation          # Grafo de navegación y rutas
├── ui                  # Capa de Presentación
│   ├── components      # Composables reutilizables (Botones, Cards)
│   ├── screens         # Pantallas (Login, Home, Detail, Admin)
│   └── theme           # Tema de Compose (Colores, Tipografía)
└── utils               # Clases de utilidad y extensiones

```

---

## 🛠️ Requisitos e Instalación

1. **Clonar el repositorio:**
```bash
git clone https://git.gonzaloromerobernal.es/IESRafaelAlberti/peco
```

2. **Abrir en Android Studio:**
* Se recomienda usar **Android Studio Ladybug** o superior.
* JDK requerido: **Java 17** o superior.


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