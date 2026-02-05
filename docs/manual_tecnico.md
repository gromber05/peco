# Manual técnico

**PECO – Documentación técnica del sistema**

---

## 1. Descripción general

PECO es una aplicación Android desarrollada en **Kotlin**, utilizando **Jetpack Compose** como sistema de interfaz gráfica y una arquitectura basada en **MVVM** con principios de **Clean Architecture**.

El objetivo del sistema es proporcionar una solución escalable, mantenible y fácilmente extensible para la gestión comunitaria de protectoras animales.

---

## 2. Tecnologías empleadas

* **Lenguaje:** Kotlin
* **Interfaz:** Jetpack Compose + Material 3
* **Arquitectura:** MVVM
* **Backend:** Firebase Firestore
* **Autenticación:** Firebase Auth
* **Inyección de dependencias:** Hilt
* **Asincronía:** Kotlin Coroutines y Flow

---

## 3. Configuración del entorno

### 3.1 Firebase

El proyecto requiere un proyecto Firebase configurado con:

* Firebase Authentication (correo y contraseña).
* Firebase Firestore para persistencia de datos.

El archivo `google-services.json` debe colocarse en el módulo `app`.

---

## 4. Estructura del proyecto

El código se organiza en capas claramente diferenciadas:

* **ui/**: pantallas Compose y componentes reutilizables.
* **data/**: repositorios y fuentes de datos.
* **model/**: modelos de dominio y estados.
* **utils/**: utilidades comunes (PDF, validaciones, etc.).

Esta separación mejora la mantenibilidad y la testabilidad.

---

## 5. Navegación

La navegación se implementa mediante **Navigation Compose**, definiendo rutas explícitas y controlando el flujo de acceso según el estado de autenticación del usuario.

---

## 6. Gestión del estado

El estado de cada pantalla se gestiona mediante **StateFlow**, expuesto desde los ViewModel.
La interfaz gráfica se limita a observar el estado y emitir eventos, manteniendo una clara separación entre lógica y presentación.

---

## 7. Funcionalidades técnicas relevantes

### 7.1 Autenticación

* Registro, inicio y cierre de sesión mediante Firebase Auth.
* Recuperación de contraseña integrada.

### 7.2 Gestión de animales

* Observación reactiva de datos desde Firestore.
* Filtrado dinámico por favoritos o animales asignados.
* Eliminación controlada mediante confirmación.

### 7.3 Informes PDF

* Generación de documentos PDF mediante APIs nativas de Android.
* Inclusión de datos filtrados y valores calculados.

### 7.4 Estadísticas y gráficos

* Cálculo de métricas en ViewModel.
* Representación gráfica básica en la interfaz.

---

## 8. Distribución de la aplicación

* **APK Debug:** para pruebas internas.
* **APK Release:** firmado para distribución.

Las rutas de salida estándar se encuentran en `app/build/outputs/apk/`.

---

## 9. Pruebas

### 9.1 Pruebas unitarias

* Ubicación: `src/test/java`
* Pruebas de ViewModels y lógica de negocio.
* Uso de MockK y Coroutines Test.

### 9.2 Ejecución

* Desde Android Studio o mediante Gradle (`./gradlew test`).
* Generación de informes HTML automáticos.

---

## 10. Limitaciones y mejoras futuras

* La carga de imágenes mediante Firebase Storage no se implementa en esta versión por limitaciones del contexto del proyecto.
* Se plantean mejoras futuras como:

  * Gráficos avanzados en informes.
  * Cache de imágenes.
  * Ampliación de la cobertura de pruebas.

---
