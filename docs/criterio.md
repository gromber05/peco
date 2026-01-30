# 📱 PECO – Plataforma de Gestión para Protectoras de Animales

---

## 📌 Descripción general del proyecto

**PECO** es una aplicación móvil Android desarrollada con **Jetpack Compose**, cuyo objetivo es **mejorar la organización y comunicación en las protectoras de animales**, facilitando la gestión de animales, adopciones y la interacción entre usuarios y voluntarios.

El proyecto responde a una **necesidad social real**, dentro de los ámbitos de:

* 🐾 **Organización social y comunitaria**
* 🌱 **Bienestar animal**
* ♿ **Accesibilidad y usabilidad digital**

Muchas protectoras gestionan su información de forma dispersa (papel, redes sociales, hojas de cálculo), lo que dificulta el seguimiento de animales y adopciones. **PECO centraliza toda esta información en una única app accesible y fácil de usar.**

---

## 🎯 Objetivos del proyecto

* Detectar una necesidad social real relacionada con el bienestar animal.
* Diseñar una solución digital funcional y usable.
* Desarrollar una aplicación Android moderna usando **Jetpack Compose**.
* Aplicar buenas prácticas de diseño, usabilidad y accesibilidad.
* Documentar técnica y funcionalmente el proyecto de forma profesional.
* Justificar decisiones de diseño y arquitectura como en un entorno real.

---

## 👥 Tipos de usuarios

La aplicación contempla **varios perfiles de usuario**, cumpliendo el requisito de mínimo dos roles:

| Rol               | Descripción                               |
| ----------------- | ----------------------------------------- |
| **Administrador** | Gestiona animales, usuarios e informes    |
| **Voluntario**    | Atiende adopciones y gestiona información |
| **Usuario**       | Consulta animales y solicita adopciones   |

---

## 🧩 Funcionalidades principales

* 📋 Listado de animales disponibles para adopción
* 🔍 Filtros y búsqueda de animales
* 🐶 Detalle completo de cada animal
* 💬 Sistema de comunicación (chat)
* 📊 Informes y estadísticas internas
* 👤 Gestión de usuarios según rol
* ⚙️ Preferencias y configuración

---

## 🎨 Diseño de interfaz

### Tecnologías y librerías usadas

* **Jetpack Compose**
* Material 3
* Navigation Compose
* ViewModel + StateFlow
* Hilt (inyección de dependencias)

### Layouts utilizados

* `Column`
* `Row`
* `Box`
* `LazyColumn`
* `LazyGrid`
* `Scaffold`

### Componentes reutilizables

* Tarjetas de animal
* Botones personalizados
* Diálogos reutilizables
* Campos de texto configurables

Cada componente acepta **parámetros y valores por defecto**, favoreciendo la reutilización y mantenibilidad.

---

## ♿ Usabilidad y accesibilidad

Se han tenido en cuenta los siguientes aspectos:

* Jerarquía visual clara
* Contraste adecuado de colores
* Tamaños de texto legibles
* Mensajes claros y comprensibles
* Distribución lógica de controles
* Interacciones simples e intuitivas

---

## 🧠 Análisis y justificación del código.

La arquitectura del proyecto sigue una separación clara por capas:

* **UI**: Pantallas Compose y componentes
* **Domain**: Lógica de negocio
* **Data**: Repositorios y fuentes de datos

Las decisiones técnicas están orientadas a:

* Escalabilidad
* Mantenibilidad
* Claridad del código
* Buenas prácticas profesionales

---

## 📊 Informes integrados.

La aplicación incluye un módulo de **informes internos**, donde se presentan:

* Recuentos de animales
* Estados de adopción
* Filtros por categorías
* Totales calculados
* Gráficos explicativos

🔧 **Herramientas utilizadas**

* Generación de informes desde datos internos
* Filtros y cálculos aplicados
* Representación gráfica

---

## 🧪 Estrategia de pruebas

### Estrategia general

Las pruebas se planifican para garantizar estabilidad, usabilidad y rendimiento antes de la publicación.

### Tipos de pruebas consideradas

* Pruebas de integración entre pantallas
* Pruebas de regresión
* Pruebas de volumen y estrés (teóricas)
* Pruebas de seguridad
* Uso de recursos (memoria y rendimiento)

Las pruebas están documentadas y relacionadas directamente con la aplicación desarrollada.

---

## 📦 Distribución de la aplicación

* Generación de APK desde Android Studio
* Firma digital de la aplicación
* Preparación para instalación y desinstalación
* Justificación de canales de distribución:

    * Instalación directa (APK)
    * Publicación futura en Google Play

Todo el proceso está **documentado y justificado**, aunque no sea obligatorio implementarlo completamente.

---

## 📘 Documentación y ayudas.

La documentación incluye:

* Manual de usuario
* Manual técnico de instalación
* Guía de uso por pantallas
* Ayudas contextuales dentro de la app
* Tutoriales explicativos
* Documentación de la estructura de datos

---

## 🎥 Evidencias incluidas.

* 📸 Capturas de la aplicación
* 📝 Documentación escrita
* 🎬 Vídeo explicativo mostrando la app paso a paso (con cámara)

---

## 🚀 Conclusión

Este proyecto demuestra la capacidad de:

* Analizar una necesidad social real
* Diseñar una solución digital coherente
* Desarrollar una aplicación Android moderna
* Documentar y justificar decisiones técnicas
* Trabajar siguiendo criterios profesionales reales
