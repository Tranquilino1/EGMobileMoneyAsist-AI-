package com.ivy.domain.agent

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class GeminiAgentService(
    private val apiKey: String
) {
    private val jsonDecoder = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val systemInstruction = """
        Eres el motor de procesamiento de lenguaje natural de EG MobileMoney. Tu tarea es escuchar el audio del usuario, extraer el monto numérico, identificar la acción y mapear los nombres de cuentas o categorías que mencione. Si el usuario dice 'Pasa 2000 de mi cuenta X a mi cuenta Y', debes mapearlo como TRANSFER, amount: 2000, source_account_name: 'X', target_account_name: 'Y'. Devuelve única y exclusivamente el JSON crudo, sin formato markdown, sin explicaciones.
    """.trimIndent()

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        },
        systemInstruction = content { text(systemInstruction) }
    )

    suspend fun processVoiceInput(audioFile: File): Result<VoiceAgentTransaction> = withContext(Dispatchers.IO) {
        try {
            val audioBytes = audioFile.readBytes()
            val response = model.generateContent(
                content {
                    blob("audio/mp4", audioBytes)
                    text("Procesa este audio de transacciones y extrae la información financiera.")
                }
            )

            val jsonText = response.text?.trim()
                ?: return@withContext Result.failure(Exception("Respuesta vacía de Gemini"))

            // Limpieza básica por si el modelo incluye marcas markdown de JSON (aunque el system prompt pide que no lo haga)
            val cleanJson = jsonText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()

            val transaction = jsonDecoder.decodeFromString<VoiceAgentTransaction>(cleanJson)
            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
