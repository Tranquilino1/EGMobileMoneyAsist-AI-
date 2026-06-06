# 📖 Manual Técnico de Ingeniería y Arquitectura

## EG Mobile Money Assist

**Autor:** Tranquilino Mba Ncogo Andeme  
**Representación:** Universidad Afro-Americana de África Central (AAUCA)  
**Proyecto Desarrollado en:** Hackathon de 24 Horas de FP Salesianos  
**Versión:** 1.0.0  
**Fecha:** Junio 2026  
**Licencia:** Apache 2.0  

---

## Índice
1. [Introducción y Objetivos](#1-introducción-y-objetivos)
2. [Requisitos del Entorno y Compilación](#2-requisitos-del-entorno-y-compilación)
3. [Arquitectura del Software (Clean Architecture + MVVM)](#3-arquitectura-del-software-clean-architecture--mvvm)
4. [Módulo de Audio y Grabación (AudioRecord)](#4-módulo-de-audio-y-grabación-audiorecord)
5. [Integración del Motor de Inteligencia Artificial (Gemini 1.5 Flash)](#5-integración-del-motor-de-inteligencia-artificial-gemini-15-flash)
6. [Modelo de Persistencia y Base de Datos (Room SQLite)](#6-modelo-de-persistencia-y-base-de-datos-room-sqlite)
7. [Inyección de Dependencias (Hilt)](#7-inyección-de-dependencias-hilt)
8. [Resolución de Problemas (Troubleshooting)](#8-resolución-de-problemas-troubleshooting)

---

## 1. Introducción y Objetivos

**EG Mobile Money Assist** es un asistente financiero personal nativo para la plataforma Android que introduce una forma revolucionaria de registro de datos financieros mediante procesamiento de lenguaje natural por voz.

El objetivo técnico principal es minimizar la fricción en el registro diario de transacciones (que suele ser la causa número uno del abandono del control financiero) integrando el modelo de lenguaje **Gemini 1.5 Flash** para procesar voz de forma síncrona en local, pero guardando y operando la base de datos de forma 100% desconectada (Offline-First) y local mediante SQLite (Room).

### Moneda del Ecosistema:
El ecosistema del proyecto opera por defecto en **XAF (Franco CFA)**, optimizando la experiencia de usuario para transacciones financieras cotidianas en Guinea Ecuatorial y la región centroafricana.

---

## 2. Requisitos del Entorno y Compilación

Para asegurar una compilación y despliegue libre de errores en sistemas locales, deben cumplirse los siguientes requerimientos:

### Software Requerido:
- **Java Development Kit (JDK)**: Versión 17 o superior.
- **Android SDK**: API level 34 o superior.
- **IDE**: Android Studio Ladybug (2024.2.1) o superior.
- **Gradle**: Build System utilizando Kotlin DSL (`.gradle.kts`).

### Variables de Entorno (Windows Powershell):
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:ANDROID_HOME = "C:\Users\RYESA\AppData\Local\Android\Sdk"
```

### Script de Compilación:
Para compilar la versión en desarrollo (Debug):
```bash
./gradlew assembleDebug
```
El APK de salida se generará en:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 3. Arquitectura del Software (Clean Architecture + MVVM)

La estructura del código fuente está dividida en tres capas de Clean Architecture para asegurar que la lógica de negocio esté completamente aislada del framework de Android y de las bases de datos externas:

### Capas de Diseño:
1. **Presentación (MVVM)**: 
   - Vistas construidas enteramente en **Jetpack Compose** (declarativo).
   - `ViewModels` que exponen estados inmutables a través de `StateFlow` y capturan los eventos de la vista (`Intentos`).
2. **Dominio**:
   - Contiene las entidades puras del negocio (`Transaction`, `Account`, `Category`).
   - Casos de Uso (`SaveVoiceTransactionUseCase`, `GetTransactionsUseCase`) que definen las acciones específicas del usuario.
   - Interfaces de Repositorios que definen el contrato de datos.
3. **Datos**:
   - Implementaciones concretas de los repositorios.
   - Base de datos Room SQLite y Daos.
   - Cliente Ktor para las peticiones de la API de Gemini.

### Estructura de Módulos Físicos:
- `app/`: Contenedor principal de configuración e inyección.
- `feature/`: Módulos modulares funcionales (`accounts`, `budget`, `edit-transaction`, `reports`).
- `shared/`: Recursos reutilizables (`data`, `domain`, `ui`).

---

## 4. Módulo de Audio y Grabación (AudioRecord)

La captura del habla se realiza a bajo nivel a través de la API `AudioRecord` de Android. Esto permite capturar la voz del usuario en búferes PCM en tiempo real.

### Implementación del Grabador (`VoiceRecorder.kt`):
La grabación se ejecuta en un hilo secundario utilizando corrutinas de Kotlin para evitar bloquear la UI:

```kotlin
class VoiceRecorder @Inject constructor(
    private val context: Context
) {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    fun startRecording(outputFile: File) {
        val sampleRate = 16000 // Frecuencia requerida por la API de voz
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("Permiso de grabación de audio denegado")
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true

        // Ejecutar escritura en archivo en hilo IO
        CoroutineScope(Dispatchers.IO).launch {
            outputFile.outputStream().use { fos ->
                val audioData = ByteArray(bufferSize)
                while (isRecording) {
                    val read = audioRecord?.read(audioData, 0, bufferSize) ?: 0
                    if (read > 0) {
                        fos.write(audioData, 0, read)
                    }
                }
            }
        }
    }

    fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}
```

El flujo guarda el archivo en formato PCM crudo y posteriormente se le concatena un encabezado WAV estándar (RIFF) de 44 bytes para que la API de Gemini pueda procesar el archivo de audio directamente sin necesidad de transcoding del lado del cliente.

---

## 5. Integración del Motor de Inteligencia Artificial (Gemini 1.5 Flash)

La transcripción del audio y el análisis de la información financiera se delegan al modelo **Gemini 1.5 Flash**. Se seleccionó este modelo por sus bajos tiempos de latencia y su alta precisión al procesar audios cortos.

### Prompt del Sistema (System Instruction):
Para garantizar que la IA no responda en texto conversacional sino que devuelva un objeto estructurado, se inyecta la siguiente instrucción de sistema (System Instruction):

```text
Eres el motor de extracción de datos financieros de la app EG Mobile Money Assist. 
Tu tarea es escuchar el audio del usuario y extraer una única transacción financiera. 
Debes responder ÚNICAMENTE con un objeto JSON válido que cumpla con el siguiente esquema:
{
  "amount": Double,       // Monto de la transacción (positivo)
  "currency": "XAF",      // Siempre debe ser XAF para transacciones locales
  "category": String,     // Nombre de la categoría (ej: Alimentación, Transporte, Ocio, Ingresos, Hogar)
  "type": "EXPENSE" | "INCOME" | "TRANSFER", // Tipo de transacción
  "description": String   // Resumen corto del gasto (ej: Cena con amigos, Compra en supermercado)
}
No agregues comentarios ni markdown. Si el usuario no menciona una divisa, asume XAF por defecto.
```

### Configuración de Ktor Client:
El cliente de red envía el audio codificado en Base64 o de forma directa en multipart binario a la API de Gemini:

```kotlin
class GeminiApiClient @Inject constructor(
    private val httpClient: HttpClient,
    private val dataStore: PreferencesDataStore
) {
    suspend fun processVoiceAudio(audioFile: File): String {
        val apiKey = dataStore.getApiKey() ?: throw IllegalStateException("API Key no configurada")
        val audioBytes = audioFile.readBytes()
        val audioBase64 = Base64.encodeToString(audioBytes, Base64.NO_WRAP)

        val response = httpClient.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(
                GeminiRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(inlineData = InlineData(mimeType = "audio/wav", data = audioBase64))
                            )
                        )
                    )
                )
            )
        }

        val geminiResponse = response.body<GeminiResponse>()
        return geminiResponse.candidates.first().content.parts.first().text
    }
}
```

---

## 6. Modelo de Persistencia y Base de Datos (Room SQLite)

Los datos financieros se persisten en local mediante una base de datos SQLite administrada por Room.

### Entidades y Relaciones Relacionales:

```kotlin
@Entity(tableName = "transaction_table")
data class TransactionEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val amount: Double,
    val currency: String = "XAF",
    val description: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val type: TransactionType,
    val categoryId: UUID,
    val accountId: UUID
)

@Entity(tableName = "category_table")
data class CategoryEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val iconKey: String,
    val colorHex: String
)
```

### Consultas DAO Avanzadas (`TransactionDao.kt`):
Utilizamos flujos reactivos de Kotlin (`Flow`) para que la interfaz se actualice de forma síncrona en tiempo real cuando se agregue una transacción por voz:

```kotlin
@Dao
interface TransactionDao {
    @Query("SELECT * FROM transaction_table ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT SUM(amount) FROM transaction_table WHERE type = 'EXPENSE' AND currency = 'XAF'")
    fun getTotalExpensesXAF(): Flow<Double?>
}
```

---

## 7. Inyección de Dependencias (Hilt)

Para mantener la arquitectura limpia y desacoplada, se inyectan las dependencias mediante **Dagger Hilt**:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "eg_mobile_money_db"
        ).build()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }
}
```

---

## 8. Resolución de Problemas (Troubleshooting)

### Error: `Problems parsing JSON / API Key invalid`
- **Causa**: La clave API introducida en el almacenamiento local seguro no es válida o ha caducado en Google AI Studio.
- **Solución**: Ir a la pantalla de Configuración en la app, verificar la conectividad de red e introducir una nueva clave activa generada desde [Google AI Studio](https://aistudio.google.com/).

### Error: `AudioRecord: initialize() failed`
- **Causa**: Falta de permisos en tiempo de ejecución o micrófono ocupado por otra app del sistema.
- **Solución**: Asegurar la solicitud de `Manifest.permission.RECORD_AUDIO` en el flujo de Onboarding de la app y comprobar que no existan otras aplicaciones de grabación activas en segundo plano.
