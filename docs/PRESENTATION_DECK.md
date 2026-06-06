# 📊 Guía de Diapositivas de Presentación para el Jurado

## EG Mobile Money Assist

**Autor y Expositor:** Tranquilino Mba Ncogo Andeme  
**Representación:** Universidad Afro-Americana de África Central (AAUCA)  
**Proyecto Desarrollado en:** Hackathon de 24 Horas de FP Salesianos  
**Versión:** 1.0.0 (Junio 2026)  

---

> [!NOTE]  
> Este documento sirve como guía impresa complementaria de las diapositivas interactivas alojadas en `docs/presentation/index.html`. Está formateado y estructurado específicamente para ser impreso o exportado a formato PDF.

---

### Diapositiva 1: Portada y Presentación Oficial

#### Contenidos en Diapositiva:
- **Título**: EG Mobile Money Assist  
- **Badge**: Desarrollado en el Hackathon de 24 Horas de FP Salesianos  
- **Representante**: AAUCA (Universidad Afro-Americana de África Central)  
- **Autor**: Tranquilino Mba Ncogo Andeme  
- **Tecnologías Clave**: Android Native + Kotlin + Jetpack Compose + Gemini 1.5 Flash  

#### Guión de Exposición (Qué decir ante el Jurado):
> *"Buenos días, distinguidos miembros del jurado. Mi nombre es Tranquilino Mba Ncogo Andeme, estudiante representante de la Universidad Afro-Americana de África Central (AAUCA). Hoy tengo el placer de presentarles **EG Mobile Money Assist**, una aplicación móvil nativa para Android desarrollada contrarreloj en este apasionante Hackathon de 24 Horas. Nuestro objetivo es resolver uno de los mayores dolores de cabeza en las finanzas del día a día usando inteligencia artificial de última generación de forma offline y segura."*

---

### Diapositiva 2: El Desafío Financiero Actual (El Problema)

#### Contenidos en Diapositiva:
- **Problema**: El 80% de los usuarios abandona el control de gastos durante la primera semana.
- **Fricción de Captura**: Registrar manualmente cada compra escribiendo montos, conceptos, seleccionar cuentas y categorías es una experiencia tediosa y lenta.
- **Invasión de Privacidad**: Las soluciones comerciales en la nube recopilan información financiera confidencial.
- **Poca Adaptación**: Escasa disponibilidad de herramientas adaptadas a monedas y formas de consumo de la región centroafricana (XAF).

#### Guión de Exposición (Qué decir ante el Jurado):
> *"Analicemos la realidad. La gestión de finanzas personales fracasa para el 80% de las personas en la primera semana. ¿Por qué? Por la fricción. Sacar el móvil, abrir una app, teclear la cantidad, buscar la categoría y guardar el recibo es un proceso lento. Si a esto le sumamos el temor a subir datos bancarios privados a nubes públicas sin control, nos damos cuenta de que el sistema actual está roto. Necesitamos una solución que sea instantánea, offline y segura."*

---

### Diapositiva 3: La Propuesta de Valor (La Solución)

#### Contenidos en Diapositiva:
- **Registro por Voz en Segundos**: Mantén pulsado un botón, di tu gasto en lenguaje natural y la app se encarga del resto.
- **Procesamiento Local Offline-First**: Persistencia de datos confidenciales al 100% en local mediante base de datos Room SQLite.
- **Moneda del Proyecto**: Operaciones y cálculos estructurados de forma nativa en **XAF (Francos CFA)**.
- **Interfaz Limpia**: Diseñada siguiendo las pautas de Material 3 y Jetpack Compose.

#### Guión de Exposición (Qué decir ante el Jurado):
> *"Nuestra propuesta es **EG Mobile Money Assist**. Consiste en eliminar por completo la fricción del teclado mediante el uso de la voz. El usuario habla de forma natural, la inteligencia artificial analiza y extrae los datos y, a su vez, respeta la privacidad de forma absoluta al mantener todos sus datos confidenciales offline, almacenados exclusivamente en el dispositivo mediante bases de datos relacionales locales."*

---

### Diapositiva 4: Arquitectura del Sistema (Ingeniería de Software)

#### Contenidos en Diapositiva:
- **Clean Architecture**: Capas desacopladas (Presentación, Dominio y Datos).
- **Patrón de UI**: MVVM (Model-View-ViewModel) reactivo.
- **Modularización**: División limpia en módulos (`app`, `feature/`, `shared/` y `widget/`) para optimizar tiempos de compilación.
- **Flujos Reactivos**: Uso de Kotlin Coroutines y StateFlow para propagar el estado de la base de datos a la UI.

#### Guión de Exposición (Qué decir ante el Jurado):
> *"Para garantizar que la app sea robusta y mantenible, hemos implementado una arquitectura limpia estructurada en capas. Separándola en Presentación, Dominio y Datos nos aseguramos de que las reglas de negocio sean independientes del motor de base de datos o de las llamadas API. El patrón MVVM nos permite tener una UI reactiva que se actualiza automáticamente cada vez que los repositorios registran un cambio."*

---

### Diapositiva 5: Simulación de Flujo de Audio a Base de Datos

#### Contenidos en Diapositiva:
- **Fase 1: Grabación**: API `AudioRecord` captura audio en formato PCM y se empaqueta en WAV.
- **Fase 2: Conectividad**: Cliente Ktor envía el audio con la API Key encriptada localmente.
- **Fase 3: Extracción**: Gemini 1.5 Flash analiza y devuelve JSON con esquema riguroso.
- **Fase 4: Room DB**: Parsea el JSON e inserta la transacción reactivamente.

#### Guión de Exposición (Qué decir ante el Jurado):
> *"Veamos cómo funciona la tecnología por detrás de escena cuando el usuario habla. Primero, la API nativa de Android graba el audio a 16 kHz. Este audio se envía directamente a la API de Gemini 1.5 Flash sin intermediarios. La IA devuelve un JSON estructurado que parseamos localmente para insertar el gasto en SQLite. Todo este proceso toma menos de 1.5 segundos en completarse."*

---

### Diapositiva 6: Persistencia Local (Room SQLite)

#### Contenidos en Diapositiva:
- **Base de Datos**: SQLite nativo a través del ORM Room.
- **Esquema de Transacciones**: Modelo relacional con llaves foráneas y borrados en cascada.
- **Room DAOs**: Flujos asíncronos reactivos (`Flow<List<Transaction>>`) para listado en tiempo real.
- **Preferences DataStore**: Almacenamiento local seguro de configuraciones y credenciales del usuario.

#### Guión de Exposición (Qué decir ante el Jurado):
> *"Nuestra base de datos Room almacena transacciones, cuentas y categorías. Al utilizar flujos asíncronos, la base de datos notifica instantáneamente a la pantalla principal en cuanto se inserta un registro. No hay consultas pesadas bloqueando la UI del usuario. Además, las claves de API privadas se guardan de forma encriptada en el almacenamiento local seguro."*

---

### Diapositiva 7: Stack Tecnológico del Ecosistema

#### Contenidos en Diapositiva:
- **Core**: Kotlin 100% nativo.
- **Diseño**: Jetpack Compose + Material 3.
- **Inyección de Dependencias**: Hilt.
- **Networking**: Ktor Client con Kotlinx Serialization.
- **Persistencia**: SQLite ORM (Room) + DataStore.
- **Servicio de Voz**: Android AudioRecord API.

#### Guión de Exposición (Qué decir ante el Jurado):
> *"Hemos utilizado las tecnologías más modernas y recomendadas por Google para el ecosistema Android. Kotlin nativo garantiza rendimiento, Jetpack Compose nos proporciona animaciones fluidas y Material 3 un diseño moderno, mientras que Hilt y Ktor administran la inyección de dependencias y el tráfico de red de manera robusta y limpia."*

---

### Diapositiva 8: Integración de Gemini AI y JSON Riguroso

#### Contenidos en Diapositiva:
- **Modelo de IA**: Gemini 1.5 Flash.
- **Ingeniería de Prompts**: System Instruction para forzar salida JSON estructurada y limitar respuestas textuales.
- **Monto y Categorización**: Mapeo semántico de categorías predefinidas e inferencia de moneda local (XAF).
- **Ejemplo de Salida JSON**:
  ```json
  {
    "amount": 5000,
    "currency": "XAF",
    "category": "Alimentación",
    "type": "EXPENSE",
    "description": "Compra en supermercado"
  }
  ```

#### Guión de Exposición (Qué decir ante el Jurado):
> *"Uno de nuestros mayores logros técnicos es obligar a Gemini 1.5 Flash a responder en un formato JSON estructurado sin añadir comentarios de texto. Mediante ingeniería de prompts y esquemas estrictos, la IA devuelve exactamente el monto numérico, la categoría correcta e infiere la divisa local XAF. Esto permite automatizar completamente la inserción de datos sin intervención del usuario."*

---

### Diapositiva 9: Conclusiones e Impacto del Proyecto

#### Contenidos en Diapositiva:
- **Proyecto Completado**: Desarrollado y desplegado de forma real en 24 Horas.
- **Accesibilidad**: Facilita el registro financiero a personas con dificultades de visión o escritura.
- **Privacidad offline**: Un gran avance frente a la tendencia de almacenamiento forzoso en la nube.
- **Escalabilidad**: Listo para agregar soporte de conversión de monedas o conexión de cuentas bancarias reales.

#### Guión de Exposición (Qué decir ante el Jurado):
> *"Para finalizar, EG Mobile Money Assist demuestra que es posible combinar el poder de la inteligencia artificial en la nube con la seguridad y privacidad offline de una base de datos local en solo 24 horas de desarrollo. Es una aplicación accesible, fluida y con un impacto real para la inclusión financiera en nuestra comunidad. Muchas gracias y quedo a su disposición para cualquier pregunta."*
