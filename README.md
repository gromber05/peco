# 🐾 PECO — Plataforma de Gestión para Protectoras de Animales

[![Android](https://img.shields.io/badge/Android-10%2B-green?logo=android)](#)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue?logo=android)](#)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple?logo=kotlin)](#)
[![Status](https://img.shields.io/badge/Status-En%20desarrollo-yellow)](#)

# 🐾 PECO – Plataforma de gestión y adopción animal

PECO es una **aplicación móvil Android** desarrollada con **Jetpack Compose**, orientada a la **organización social y comunitaria**, cuyo objetivo es mejorar la gestión interna de una protectora de animales y facilitar el proceso de adopción responsable.

El proyecto se ha desarrollado como **Proyecto Final del módulo DIN**, aplicando una arquitectura moderna, buenas prácticas de desarrollo y una documentación completa tanto a nivel de usuario como técnico.

---

## 📌 Objetivos del proyecto

* Centralizar la información de animales disponibles para adopción.
* Facilitar la comunicación entre usuarios, voluntarios y administradores.
* Diferenciar roles y permisos dentro de la aplicación.
* Generar informes estructurados a partir de datos reales.
* Aplicar una arquitectura clara, mantenible y testable.
* Responder a una **necesidad social real** mediante una solución digital.

---

## 🧱 Arquitectura y tecnologías

* **Lenguaje:** Kotlin
* **Interfaz gráfica:** Jetpack Compose + Material 3
* **Arquitectura:** MVVM + principios de Clean Architecture
* **Persistencia:** Firebase Firestore
* **Autenticación:** Firebase Auth
* **Inyección de dependencias:** Hilt
* **Asincronía:** Kotlin Coroutines + Flow

---

## 🧭 Funcionalidades principales

* Registro e inicio de sesión de usuarios.
* Gestión de perfiles y roles (usuario, voluntario, administrador).
* Listado y detalle de animales.
* Favoritos y filtrado dinámico.
* Gestión de animales asignados (“Mis animales”).
* Eliminación segura con confirmación.
* Generación de informes en **PDF**.
* Panel de estadísticas con gráficos básicos.
* Recuperación de contraseña.

---

## 👤 Roles de usuario

| Rol               | Funcionalidad                       |
| ----------------- | ----------------------------------- |
| **Administrador** | Gestión global, informes y usuarios |
| **Voluntario**    | Gestión de animales y adopciones    |
| **Usuario**       | Consulta y solicitudes de adopción  |

---

## 📂 Estructura del repositorio

```text
.
├── app/                      # Código fuente de la aplicación
├── docs/                     # Documentación del proyecto
│   ├── criterio.md           # Documentación principal (RA y rúbrica)
│   ├── pruebas.md            # Estrategia y documentación de pruebas
│   ├── manual_tecnico.md     # Manual técnico de la aplicación
│   ├── manual_de_usuario.md  # EManual de usuario 
├── README.md                 # Descripción general del proyecto
```

---

## 📚 Documentación del proyecto

Toda la documentación del proyecto se encuentra organizada en la carpeta `docs/`:

### 📖 Manuales

* 👤 **Manual de usuario**
  👉 [docs/manual_usuario.md](docs/manual_usuario.md)

* 🛠️ **Manual técnico**
  👉 [docs/manual_tecnico.md](docs/manual_tecnico.md)

### 🧪 Pruebas

* 🧪 **Documento de pruebas y validación**
  👉 [docs/pruebas.md](docs/pruebas.md)

### 📐 Evaluación y criterios

* 📐 **Criterios de evaluación y justificación**
  👉 [docs/criterio.md](docs/criterio.md)

---

## 🧪 Pruebas y calidad

El proyecto incluye **pruebas unitarias** centradas en la lógica de negocio (ViewModels y repositorios), utilizando:

* JUnit
* MockK
* Coroutines Test
* Turbine

Las pruebas se encuentran en:

```
app/src/test/java/
```

Y pueden ejecutarse mediante:

```bash
./gradlew test
```

Los informes de resultados se generan automáticamente en formato HTML.

---

## 📦 Distribución

La aplicación puede generarse en formato APK:

* **Debug APK:** para pruebas internas.
* **Release APK:** firmado para distribución.

Ruta típica de salida:

```
app/build/outputs/apk/
```

---

## ⚠️ Limitaciones conocidas

* La carga de imágenes mediante Firebase Storage no se ha implementado en esta versión por limitaciones del contexto del proyecto.
* El modelo de datos mantiene el campo `photo` preparado para una futura ampliación.

Estas limitaciones y posibles mejoras se detallan en la documentación técnica.

---

## 🚀 Mejoras futuras

* Integración de imágenes con Firebase Storage o servicio externo.
* Gráficos avanzados también en informes PDF.
* Aumento de la cobertura de pruebas automatizadas.
* Optimización visual y accesibilidad.

---

## 👨‍💻 Autor

Proyecto desarrollado por **Gonzalo Romero Bernal**
Ciclo Formativo de Grado Superior – Desarrollo de Aplicaciones Multiplataforma (2º DAM)

---