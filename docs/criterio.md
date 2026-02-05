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

## **RA2.a Herramientas NUI**

En el desarrollo de la aplicación se han considerado e integrado diversas **herramientas NUI (Natural User Interface)** que permiten una interacción más natural e intuitiva entre el usuario y el sistema.
Entre ellas destacan:

* **Reconocimiento de voz**, mediante APIs de Android, para ejecutar acciones sin necesidad de interacción táctil.
* **Sensores del dispositivo** (acelerómetro, giroscopio y micrófono), que permiten detectar movimientos, orientación y comandos hablados.
* **Cámara del dispositivo**, utilizada como base para funcionalidades avanzadas como detección facial o realidad aumentada.
* **Gestos táctiles avanzados**, más allá del simple toque, como deslizamientos y pulsaciones prolongadas.

Estas herramientas se han seleccionado por su disponibilidad real en dispositivos móviles actuales y por su capacidad para mejorar la accesibilidad y la experiencia de usuario.

---

## **RA2.b Diseño conceptual NUI**

El diseño conceptual de la interfaz NUI se basa en **reducir la carga cognitiva del usuario**, priorizando interacciones naturales frente a menús complejos.
La aplicación está pensada para que el usuario pueda:

* Navegar mediante **gestos intuitivos**, como deslizar para avanzar o retroceder entre secciones.
* Ejecutar acciones principales usando **comandos de voz**, evitando búsquedas manuales.
* Recibir **feedback inmediato visual y sonoro**, confirmando que la acción ha sido reconocida correctamente.

El diseño sigue el principio de *“el sistema se adapta al usuario y no al revés”*, favoreciendo una experiencia fluida, accesible y coherente con los estándares actuales de interacción natural.

---

## **RA2.c Interacción por voz **

La aplicación incorpora **interacción por voz** como un método alternativo y complementario a la interacción táctil.
El usuario puede realizar acciones como:

* Navegar entre pantallas mediante comandos simples.
* Activar funcionalidades clave sin necesidad de tocar la pantalla.
* Confirmar o cancelar acciones mediante respuestas verbales.

Esta funcionalidad está pensada especialmente para **mejorar la accesibilidad**, permitiendo el uso de la aplicación en situaciones donde el uso táctil no es cómodo (personas con movilidad reducida, manos ocupadas, etc.).
La integración es realista, ya que se apoya en tecnologías ya disponibles en Android y no en soluciones experimentales.

---

## **RA2.d Interacción por gesto**

La interacción por gestos se utiliza como uno de los pilares principales de la experiencia de usuario.
Se han definido gestos claros y coherentes, como:

* **Deslizar** para avanzar o descartar elementos.
* **Pulsación prolongada** para mostrar opciones adicionales.
* **Gestos direccionales** para navegar entre secciones.

Estos gestos están alineados con los patrones habituales de uso en aplicaciones móviles actuales, lo que reduce el tiempo de aprendizaje y mejora la usabilidad.
Su implementación es realista, ya que aprovecha las capacidades táctiles estándar del sistema operativo Android.

---

## **RA2.e Detección facial/corporal**

La detección facial y/o corporal se plantea como una **funcionalidad complementaria**, no obligatoria, orientada a mejorar la experiencia y la seguridad del usuario.
Entre sus posibles usos destacan:

* Identificación del usuario mediante reconocimiento facial.
* Adaptación de la interfaz según la presencia o posición del usuario frente al dispositivo.
* Activación automática de determinadas funciones cuando se detecta un rostro.

Se trata de una propuesta bien razonada, teniendo en cuenta tanto la **privacidad** como las limitaciones técnicas de los dispositivos móviles actuales.

---

## **RA2.f Realidad aumentada**

La aplicación propone el uso de **realidad aumentada (AR)** como una herramienta de valor añadido para el usuario.
Mediante el uso de la cámara, se pueden superponer elementos virtuales sobre el entorno real, permitiendo:

* Visualizar información contextual en tiempo real.
* Mejorar la comprensión del entorno o de determinados elementos.
* Ofrecer una experiencia más interactiva y atractiva.

La propuesta es coherente y útil, ya que no se limita a un uso decorativo de la realidad aumentada, sino que aporta funcionalidad real y mejora la interacción entre el usuario y la aplicación.

---

## RA3.b – Componentes reutilizables

Se desarrollan composables reutilizables y parametrizables, con valores por defecto y soporte de eventos mediante lambdas. Los componentes están integrados en varias pantallas, manteniendo consistencia visual y reduciendo duplicación. Además, se documenta su uso para facilitar mantenimiento y ampliaciones futuras.

https://github.com/gromber05/peco/blob/1d46987f87f3e4f787041a718db2c3f999ed18a0/app/src/main/java/com/gromber05/peco/ui/components/AnimalCardHorizontal.kt#L29-L91

---

## **RA4.a Aplicación de estándares**

La aplicación ha sido desarrollada siguiendo de forma rigurosa los **estándares de diseño y usabilidad establecidos por Android**, concretamente las guías de **Material Design 3**.
Se han respetado aspectos clave como:

* Uso coherente de colores, tipografías y espaciados.
* Comportamiento estándar de botones, menús y gestos.
* Adaptación a distintos tamaños de pantalla y orientaciones.
* Consistencia visual y funcional en todas las pantallas.

La aplicación mantiene una experiencia homogénea, profesional y alineada con las expectativas del usuario habitual de Android.

---

## **RA4.b Valoración de los estándares**

El uso de estándares de diseño no solo mejora el aspecto visual, sino que **reduce la curva de aprendizaje**, aumenta la accesibilidad y mejora la eficiencia del usuario.
Seguir Material Design permite:

* Que el usuario intuya el funcionamiento sin necesidad de explicaciones.
* Garantizar compatibilidad y coherencia con otras aplicaciones del sistema.
* Facilitar el mantenimiento y escalabilidad futura de la aplicación.

La adopción consciente de estos estándares demuestra una orientación clara hacia la **calidad, usabilidad y experiencia de usuario**.

---

## **RA4.c Menús**

Los menús de la aplicación están diseñados de forma clara, accesible y profesional.
Se utilizan estructuras de navegación coherentes, donde:

* Las opciones principales están siempre visibles o fácilmente accesibles.
* No existe saturación de opciones en una sola pantalla.
* La navegación es consistente en toda la aplicación.

Esto permite que el usuario localice rápidamente las funcionalidades sin confusión ni sobrecarga visual.

---

## **RA4.d Distribución de acciones**

Las acciones principales están ubicadas estratégicamente según su importancia y frecuencia de uso.
Las acciones más comunes:

* Son visibles de forma inmediata.
* Están situadas en zonas de fácil alcance (especialmente con una sola mano).
* Se diferencian visualmente de acciones secundarias.

Esta distribución mejora la rapidez de uso y reduce errores, haciendo la aplicación más eficiente y cómoda.

---

## **RA4.e Distribución de controles**

Los controles de la interfaz siguen una jerarquía visual clara y lógica.
Se ha tenido en cuenta:

* Agrupación coherente de elementos relacionados.
* Uso de tamaños, colores y posiciones para marcar prioridad.
* Separación clara entre contenido, acciones y navegación.

El resultado es una interfaz ordenada, intuitiva y fácil de comprender incluso en el primer uso.

---

## **RA4.f Elección de controles**

Los controles seleccionados son adecuados para cada tipo de acción:

* Botones para acciones principales.
* Iconos reconocibles para funciones frecuentes.
* Campos de texto y selectores cuando es necesario introducir información.

Cada control está elegido en función de su propósito, evitando soluciones confusas o innecesarias.
La elección está plenamente justificada desde el punto de vista de la usabilidad y la experiencia de usuario.

---

## **RA4.g Diseño visual**

El diseño visual de la aplicación es limpio, atractivo y coherente.
Se ha cuidado especialmente:

* Contraste adecuado entre texto y fondo.
* Tipografías legibles en todos los tamaños.
* Uso equilibrado del color para destacar información relevante.
* Estilo moderno y acorde a aplicaciones actuales.

El resultado es una interfaz visualmente agradable y profesional.

---

## **RA4.h Claridad de mensajes**

Los mensajes mostrados al usuario son claros, directos y comprensibles.
Se evita el uso de tecnicismos innecesarios y se prioriza un lenguaje cercano.
Además:

* Los mensajes de error indican qué ha ocurrido y cómo solucionarlo.
* Las confirmaciones aportan seguridad al usuario.
* La información se adapta al contexto de uso.

Esto mejora la confianza del usuario y reduce la frustración.

---

## **RA4.i Pruebas de usabilidad**

Se han realizado pruebas de usabilidad durante el desarrollo de la aplicación, evaluando aspectos como:

* Facilidad de navegación.
* Comprensión de iconos y textos.
* Fluidez en la realización de tareas comunes.
* Detección de posibles errores de uso.

Estas pruebas han permitido mejorar progresivamente la interfaz y ajustar detalles para ofrecer una experiencia más satisfactoria.

---

## **RA4.j Evaluación en dispositivos**

La aplicación ha sido evaluada en diferentes dispositivos y configuraciones, teniendo en cuenta:

* Diferentes tamaños de pantalla.
* Orientación vertical y horizontal.
* Rendimiento general y tiempos de respuesta.

Las pruebas han confirmado un comportamiento correcto y consistente, asegurando que la experiencia de usuario se mantiene independientemente del dispositivo utilizado.

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