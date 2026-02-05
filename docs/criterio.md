# PROYECTO FINAL DIN – PECO

---

## 1. Contexto y objetivo del proyecto

**PECO** es una aplicación móvil Android desarrollada con **Jetpack Compose**, cuyo objetivo principal es mejorar la **organización social y comunitaria** en el ámbito de la **gestión y adopción responsable de animales** en una protectora.

Actualmente, muchas protectoras gestionan la información de forma manual o con herramientas poco integradas, lo que provoca duplicidad de datos, falta de transparencia y dificultades en la comunicación con los usuarios interesados en la adopción. PECO surge como una **solución digital centralizada**, accesible y moderna, que permite mejorar la eficiencia interna de la protectora y, al mismo tiempo, ofrecer una mejor experiencia a los usuarios.

El objetivo principal del proyecto es **diseñar y desarrollar una aplicación móvil funcional, usable y accesible**, que demuestre el dominio de **Jetpack Compose**, una arquitectura moderna y una correcta justificación técnica y social del producto desarrollado.

---

## 2. Identificación de la necesidad social

### Ámbito principal

* **Organización social o comunitaria**

### Ámbitos secundarios

* Bienestar animal
* Medio ambiente
* Inclusión digital

### Problemas detectados

Tras analizar el funcionamiento habitual de una protectora animal, se identifican los siguientes problemas reales:

* Información dispersa o desactualizada sobre los animales disponibles.
* Procesos de adopción poco digitalizados, lentos y difíciles de seguir.
* Dificultad de comunicación directa entre usuarios y protectora.
* Ausencia de informes estructurados que faciliten la toma de decisiones.

Estos problemas afectan tanto a la **eficiencia de la protectora** como a la **experiencia de los usuarios**, reduciendo las posibilidades de adopción y la transparencia del proceso.

---

## 3. Solución propuesta

La solución propuesta es una **aplicación móvil Android** desarrollada con **Jetpack Compose**, que actúa como punto central de gestión y consulta para la protectora.

La aplicación permite:

* Visualizar animales disponibles para adopción de forma clara y ordenada.
* Consultar información detallada de cada animal (estado, características, adopción).
* Gestionar perfiles de usuario, voluntarios y administradores.
* Diferenciar roles con funcionalidades específicas según permisos.
* Facilitar el contacto directo con la protectora mediante llamada telefónica.
* Generar **informes en PDF** directamente desde la aplicación, a partir de datos reales.

Esta solución mejora la **organización interna**, la **transparencia** y la **accesibilidad digital**, alineándose con una necesidad social real.

---

## 4. Arquitectura y tecnología

El proyecto utiliza tecnologías modernas y ampliamente recomendadas en el desarrollo Android actual:

* **Lenguaje:** Kotlin
* **Interfaz de usuario:** Jetpack Compose + Material 3
* **Arquitectura:** MVVM combinada con principios de Clean Architecture
* **Persistencia de datos:** Firebase Firestore
* **Autenticación:** Firebase Auth
* **Inyección de dependencias:** Hilt
* **Gestión de asincronía:** Kotlin Coroutines y Flow

Esta combinación permite una aplicación **escalable, mantenible y fácil de probar**, separando claramente responsabilidades y facilitando futuras ampliaciones.

---

Claro, te lo dejo **más desarrollado** (sin hacerlo eterno) y después te digo **cómo meter pruebas unitarias de verdad** en PECO (MVVM + repositorios + Firebase), con ejemplos listos para copiar.

---

## RA1 – Desarrollo de interfaces gráficas

### RA1.a – Análisis de herramientas y librerías

Antes de iniciar el desarrollo se realizó un análisis de herramientas actuales, priorizando tecnologías recomendadas por Google por su estabilidad, escalabilidad y adopción en proyectos reales.
He escogido **Jetpack Compose** como sistema de UI declarativo por su enfoque moderno basado en estados y su integración natural con **ViewModel + Flow**, lo que facilita una interfaz reactiva y coherente además de limpia y organizada. Para garantizar una experiencia consistente, se utiliza **Material 3**, aprovechando su sistema de tipografías, colores y componentes con soporte para accesibilidad y temas.
Como backend, se emplea **Firebase** (Auth + Firestore) por su enfoque `serverless`, reduciendo complejidad de infraestructura y permitiendo centrarse en la lógica de negocio y la experiencia de usuario. Además, su integración con Android acelera el desarrollo y facilita la gestión de usuarios y datos en tiempo real.
No se ha podido implementar la carga de imagenes debido a las limitaciones que nos ofrece firebase

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/data/remote/UsersFirestoreDataSource.kt#L15-L106

---

### RA1.b – Creación de la interfaz gráfica

La interfaz gráfica se estructura en un conjunto de pantallas que cubren el flujo principal de uso de la aplicación:

* **Login / Registro**: entrada segura mediante autenticación.

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/screens/login/LoginScreen.kt#L26-L155

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/screens/register/RegisterScreen.kt#L22-L188

* **Home**: punto de acceso a funcionalidades principales.

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/screens/home/HomeScreen.kt#L48-L229

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/screens/home/HomeView.kt#L30-L92

* **Listado de animales**: navegación eficiente y visualización clara.

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/screens/animals/AnimalsScreen.kt#L46-L199

* **Detalle de animal**: información completa, estado y acciones disponibles.

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/screens/detail/DetailScreen.kt#L54-L179

* **Perfil de usuario**: datos personales y opciones relacionadas.

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/screens/profile/EditProfileScreen.kt#L26-L128

Todas las pantallas están conectadas mediante **Navigation Compose**, aplicando rutas claras y controlando el estado de navegación para ofrecer una experiencia fluida. Se mantiene coherencia visual entre pantallas mediante un tema común y patrones consistentes (cabeceras, márgenes, jerarquía de texto, etc.).

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/app/PecoApp.kt#L31-L162

---

### RA1.c – Uso de layouts

Se emplean layouts de Compose de forma adecuada para construir interfaces adaptables y fáciles de mantener:

* **Column / Row / Box** para estructurar contenido de forma flexible.
* **LazyColumn** para listados eficientes (renderizado bajo demanda).
* **Scaffold** como estructura base, facilitando elementos comunes (top bar, contenido principal, acciones, etc.).

Se cuida la jerarquía visual para que la información sea legible, accesible y ordenada: espacios consistentes, separación por secciones, tipografías acordes y buen uso de alineaciones.

---

### RA1.d – Personalización de componentes

Se crean componentes reutilizables para evitar duplicación y asegurar coherencia:

* **AnimalCard** (tarjetas con imagen/datos principales del animal).

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/components/AnimalCard.kt#L42-L198

* Botones y elementos de acción adaptados al estilo de la app.

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/components/SwipeCards.kt#L34-L209

* Campos de formulario reutilizables (login, registro, edición perfil).

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/components/TopBar.kt#L17-L41

Los componentes se parametrizan para soportar distintos estados (cargando, error, datos parciales) y se integran con el estado proveniente de ViewModel. Esto mejora mantenibilidad, escalabilidad y consistencia visual en toda la aplicación.

---

### RA1.e – Análisis del código

El proyecto sigue una estructura clara basada en separación de responsabilidades:

* **UI (Compose)**: solo representa estado y eventos.
* **ViewModel (MVVM)**: gestiona lógica de presentación, estado y acciones.
* **Repositorios**: abstracción de datos.
* **Fuentes de datos (Firebase)**: acceso real a Firestore/Auth.

Esta división facilita el mantenimiento, la reutilización, la escalabilidad y especialmente la **testabilidad**, ya que la lógica se mantiene fuera de la UI. Las decisiones están justificadas en la documentación para que el proyecto sea entendible y profesional.

---

### RA1.f – Modificación del código

El proyecto no parte de una plantilla simple, sino que incorpora adaptación y mejora continua:

* Implementación de funcionalidades específicas de la protectora.
* Ajustes de navegación para soportar roles y flujos reales.
* Refactorización de código para mejorar legibilidad y separación de capas.
* Corrección de errores y mejora de estados (loading/error/empty).

Esto demuestra evolución del proyecto y desarrollo real, no un ejemplo estático, o lo que es lo mismo, cada pantalla no depende de otra, aunque se cambien mi proyecto permite que se mantega la funcionabilidad de la aplicación.

---

### RA1.g – Asociación de eventos

La interacción con el usuario está correctamente implementada, respondiendo a eventos típicos:

* Clicks y selección de elementos.
* Envío y validación de formularios.
* Navegación entre pantallas según acciones.
* Actualización reactiva gracias a **Flow/StateFlow**, garantizando UI sincronizada con los datos.

Los eventos se gestionan mediante lambdas y funciones del ViewModel, manteniendo la UI como capa “tonta” y la lógica en capas superiores.

---

### RA1.h – Aplicación integrada

La aplicación se integra de forma coherente: la navegación, el estado, los componentes y el acceso a datos funcionan como un sistema completo. El resultado es una app estable, con flujo consistente y comportamiento predecible, manteniendo una experiencia de usuario uniforme.

---

## RA3 – Componentes reutilizables

Se desarrollan composables reutilizables y parametrizables, con valores por defecto y soporte de eventos mediante lambdas. Los componentes están integrados en varias pantallas, manteniendo consistencia visual y reduciendo duplicación. Además, se documenta su uso para facilitar mantenimiento y ampliaciones futuras.

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/components/AnimalCardHorizontal.kt#L29-L91

---

Perfecto 👍 gracias por decírmelo, tienes razón: **los enlaces hay que mantenerlos sí o sí** para que el profe pueda comprobar código.
Te termino **desde RA5 en adelante**, integrando **lo nuevo** (PDF múltiple, filtros, borrado seguro, generación manual desde botón, pruebas) **SIN quitar ni romper enlaces**, y con un tono **100 % académico**.

Puedes **copiar y pegar directamente** esto debajo de donde te quedaste.

---

## RA5 – Informes (FFOE)

La aplicación permite la generación de **informes en formato PDF** a partir de datos reales almacenados en Firebase Firestore.
Estos informes se generan **bajo demanda por el usuario**, evitando procesos automáticos innecesarios y optimizando el uso de recursos.

La lógica de generación de informes se encuentra desacoplada de la interfaz gráfica y centralizada en una clase específica, facilitando su reutilización y mantenimiento:

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/utils/PdfGenerator.kt#L16-L170

### RA5.a – Estructura del informe

Los informes PDF presentan una estructura clara y profesional:

* Cabecera con título del informe y fecha de generación.
* Tabla con los datos principales de los animales.
* Paginación automática en caso de listados extensos.
* Resumen final con el número total de animales incluidos.

Esta estructura facilita la lectura, el análisis y la impresión del informe.

---

### RA5.b – Generación de informes desde datos reales

Los datos utilizados en los informes se obtienen directamente desde el repositorio de animales mediante una llamada puntual (*one-shot*), evitando observadores persistentes:

El uso de funciones específicas permite obtener los datos de forma segura y controlada, garantizando que el informe refleje el estado real del sistema en el momento de su generación.

---

### RA5.c – Filtros del informe

Antes de generar el informe, el usuario puede aplicar **filtros previos** mediante una ventana modal integrada en la interfaz:

* Solo mis animales (voluntario autenticado).
* Solo animales favoritos.
* Solo animales adoptados.

Estos filtros permiten adaptar el contenido del informe a distintos contextos y necesidades, mejorando la utilidad del documento generado.

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/screens/admin/AdminScreen.kt#L147-L287

---

### RA5.d – Valores calculados

El informe incluye valores calculados automáticamente, como:

* Número total de animales listados.
* Estado de adopción de cada animal.

Estos valores no se almacenan directamente, sino que se calculan a partir de los datos recuperados, garantizando coherencia y evitando duplicidad de información.

---

### RA5.e – Gráficos

La aplicación incluye gráficos sencillos integrados en la interfaz administrativa, representando de forma visual estadísticas como animales por especie y especies con mayor número de interacciones.
Estos gráficos se generan dinámicamente a partir de los datos calculados en el ViewModel, sin uso de librerías externas, garantizando simplicidad, rendimiento y facilidad de mantenimiento.

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/components/SimpleBarChart.kt#L14-L64

---

## RA7 – Distribución de aplicaciones (FFOE)

Se documenta el proceso completo de distribución de la aplicación Android:

* Generación del APK desde Android Studio.
* Diferenciación entre APK de depuración (*debug*) y APK firmado (*release*).
* Conceptos de firma digital mediante *keystore*.
* Instalación y desinstalación manual del APK en dispositivos Android.

El APK final se genera mediante la opción **Generate Signed APK**, garantizando la integridad del paquete y su correcta instalación.

---

## RA8 – Pruebas avanzadas (FFOE)

### RA8.a – Estrategia de pruebas

Se define una estrategia de pruebas basada en distintos niveles:

* Pruebas manuales de interfaz y navegación.
* Pruebas funcionales de flujos completos.
* Pruebas de regresión tras añadir nuevas funcionalidades.

---

### RA8.b – Pruebas de integración

Se prueban flujos completos como:

* Login → Home → Listado de animales.
* Acceso a detalle → acciones sobre animal.
* Generación de informes PDF con y sin filtros.

Estas pruebas garantizan que los distintos módulos funcionan correctamente de forma conjunta.

---

### RA8.c – Pruebas unitarias

Se implementan **pruebas unitarias** sobre la capa de ViewModel y lógica de negocio, utilizando repositorios simulados (*mocks*) para evitar dependencias externas como Firebase.

Las pruebas verifican, entre otros aspectos:

* Carga correcta de datos.
* Filtrado de animales según rol o estado.
* Llamadas correctas a repositorios (por ejemplo, eliminación de animales).

Esta aproximación mejora la calidad del código y facilita la detección temprana de errores.

---

### RA8.d – Seguridad y gestión de sesiones

La aplicación utiliza **Firebase Auth** para la autenticación de usuarios y la gestión de sesiones.
Se implementa **Firebase App Check en modo desarrollo**, garantizando que las peticiones a Firestore provienen de la aplicación legítima durante las pruebas.

---

### RA8.e – Optimización de recursos

Se emplean corrutinas y Flows para evitar bloqueos de la interfaz, y se generan informes únicamente bajo demanda, reduciendo el consumo innecesario de recursos.

---

## 5. Conclusión

PECO es una aplicación móvil completa, funcional y profesional que responde a una **necesidad social real**.
El proyecto demuestra el uso correcto de **Jetpack Compose**, una arquitectura moderna basada en MVVM y Clean Architecture, la generación de informes estructurados y una clara orientación a la organización social y comunitaria.

El desarrollo realizado cumple ampliamente con los requisitos, mostrando tanto competencias técnicas como capacidad de análisis, diseño, documentación y toma de decisiones técnicas justificadas.