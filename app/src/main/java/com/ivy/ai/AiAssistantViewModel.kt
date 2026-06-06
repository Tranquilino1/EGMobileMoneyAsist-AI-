package com.ivy.ai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.BudgetDao
import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.ui.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import com.ivy.legacy.domain.deprecated.logic.AccountCreator
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.domain.usecase.account.AccountBalanceUseCase
import com.ivy.data.model.AccountId

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiAssistantState(
    val messages: List<ChatMessage>,
    val isLoading: Boolean,
    val inputText: String,
    val totalBalance: Double
)

sealed interface AiAssistantEvent {
    data class ChangeInput(val text: String) : AiAssistantEvent
    object SendMessage : AiAssistantEvent
    object ClearChat : AiAssistantEvent
    data class LinkBank(val bankName: String, val balance: Double, val details: String) : AiAssistantEvent
}

@Serializable
data class GeminiRequest(
    val contents: List<ContentRequest>,
    val systemInstruction: SystemInstruction? = null
)

@Serializable
data class ContentRequest(
    val role: String,
    val parts: List<PartRequest>
)

@Serializable
data class PartRequest(
    val text: String
)

@Serializable
data class SystemInstruction(
    val parts: List<PartRequest>
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: ContentResponse? = null
)

@Serializable
data class ContentResponse(
    val parts: List<PartResponse>? = null
)

@Serializable
data class PartResponse(
    val text: String
)

@HiltViewModel
class AiAssistantViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao,
    private val httpClient: HttpClient,
    private val accountCreator: AccountCreator,
    private val accountBalanceUseCase: AccountBalanceUseCase
) : ComposeViewModel<AiAssistantState, AiAssistantEvent>() {

    private val messages = mutableStateListOf<ChatMessage>()
    private val isLoading = mutableStateOf(false)
    private val inputText = mutableStateOf("")
    private val totalBalance = mutableStateOf(0.0)

    private val geminiApiKey = "AQ.Ab8RN6IUNWTzNV4vU6FodeKIyBmH-EsYSLjI434Hq8kEK44G5A"
    private val geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$geminiApiKey"

    init {
        // Add initial welcoming message
        messages.add(
            ChatMessage(
                id = "welcome",
                text = "¡Hola! Soy tu asistente financiero de EG MobileMoney Asist. Estoy aquí para ayudarte a controlar tus gastos y priorizar lo que realmente importa. ¿En qué puedo ayudarte hoy?",
                isUser = false
            )
        )
        refreshFinanceData()
    }

    private fun refreshFinanceData() {
        viewModelScope.launch {
            try {
                val accounts = accountDao.findAll()
                var sum = 0.0
                for (acc in accounts) {
                    val balanceMap = accountBalanceUseCase.calculate(AccountId(acc.id))
                    sum += balanceMap.values.sumOf { it.value }
                }
                totalBalance.value = sum
            } catch (e: Exception) {
                // Ignore or log
            }
        }
    }

    @Composable
    override fun uiState(): AiAssistantState {
        LaunchedEffect(Unit) {
            refreshFinanceData()
        }
        return AiAssistantState(
            messages = messages.toList(),
            isLoading = isLoading.value,
            inputText = inputText.value,
            totalBalance = totalBalance.value
        )
    }

    override fun onEvent(event: AiAssistantEvent) {
        when (event) {
            is AiAssistantEvent.ChangeInput -> {
                inputText.value = event.text
            }
            AiAssistantEvent.SendMessage -> {
                sendMessage()
            }
            is AiAssistantEvent.LinkBank -> {
                linkBankAccount(event.bankName, event.balance, event.details)
            }
            AiAssistantEvent.ClearChat -> {
                messages.clear()
                messages.add(
                    ChatMessage(
                        id = "welcome",
                        text = "¡Hola! Soy tu asistente financiero de EG MobileMoney Asist. Estoy aquí para ayudarte a controlar tus gastos y priorizar lo que realmente importa. ¿En qué puedo ayudarte hoy?",
                        isUser = false
                    )
                )
            }
        }
    }

    private fun linkBankAccount(bankName: String, balance: Double, accountDetails: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                
                // Color picking for banks
                val color = when (bankName.lowercase()) {
                    "ecobank" -> androidx.compose.ui.graphics.Color(0xFF0D9488) // Teal
                    "muni dinero" -> androidx.compose.ui.graphics.Color(0xFFF59362) // Orange
                    "cceibank" -> androidx.compose.ui.graphics.Color(0xFF3193F5) // Blue
                    "b-mori" -> androidx.compose.ui.graphics.Color(0xFF12B880) // Green
                    "geleto" -> androidx.compose.ui.graphics.Color(0xFFF5D018) // Yellow
                    "bettomax" -> androidx.compose.ui.graphics.Color(0xFFF53D3D) // Red
                    "1xbet" -> androidx.compose.ui.graphics.Color(0xFF5C3DF5) // Purple
                    "bgfibank" -> androidx.compose.ui.graphics.Color(0xFF303033) // Dark Gray
                    else -> androidx.compose.ui.graphics.Color(0xFF10B981) // Emerald Green
                }

                val icon = when (bankName.lowercase()) {
                    "ecobank", "cceibank", "bgfibank" -> "bank"
                    "muni dinero", "b-mori", "geleto" -> "wallet"
                    else -> "card"
                }

                val displayName = "$bankName ($accountDetails)"

                // Call AccountCreator to handle insertion and initial balance transactions
                accountCreator.createAccount(
                    com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData(
                        name = displayName,
                        currency = "XAF",
                        color = color,
                        icon = icon,
                        balance = balance,
                        includeBalance = true
                    )
                ) {
                    refreshFinanceData()
                }

                messages.add(
                    ChatMessage(
                        id = java.util.UUID.randomUUID().toString(),
                        text = "¡Éxito de Sincronización! He establecido una vinculación digital con **$bankName** ($accountDetails) y cargado un saldo inicial de **$balance XAF**. He configurado las reglas de prioridades para este canal.",
                        isUser = false
                    )
                )
            } catch (e: Exception) {
                messages.add(
                    ChatMessage(
                        id = java.util.UUID.randomUUID().toString(),
                        text = "Error al vincular cuenta digital: ${e.localizedMessage}",
                        isUser = false
                    )
                )
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun sendMessage() {
        val query = inputText.value.trim()
        if (query.isEmpty() || isLoading.value) return

        val userMessage = ChatMessage(
            id = java.util.UUID.randomUUID().toString(),
            text = query,
            isUser = true
        )
        messages.add(userMessage)
        inputText.value = ""
        isLoading.value = true

        viewModelScope.launch {
            try {
                // Fetch context data
                val accounts = accountDao.findAll()
                val transactions = transactionDao.findAll().take(50) // take last 50 transactions
                val categories = categoryDao.findAll()
                val budgets = budgetDao.findAll()

                // Calculate balances reactively
                val accountBalances = accounts.associate { acc ->
                    val balanceMap = accountBalanceUseCase.calculate(AccountId(acc.id))
                    acc.id to balanceMap.values.sumOf { it.value }
                }

                val systemPrompt = buildSystemPrompt(accounts, accountBalances, transactions, categories, budgets)

                // Build history
                val contentsList = messages.map { msg ->
                    ContentRequest(
                        role = if (msg.isUser) "user" else "model",
                        parts = listOf(PartRequest(text = msg.text))
                    )
                }

                val request = GeminiRequest(
                    contents = contentsList,
                    systemInstruction = SystemInstruction(parts = listOf(PartRequest(text = systemPrompt)))
                )

                val response: GeminiResponse = httpClient.post(geminiUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }.body()

                val assistantText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Lo siento, no pude procesar tu solicitud. Por favor intenta de nuevo."

                messages.add(
                    ChatMessage(
                        id = java.util.UUID.randomUUID().toString(),
                        text = assistantText,
                        isUser = false
                    )
                )
            } catch (e: Exception) {
                messages.add(
                    ChatMessage(
                        id = java.util.UUID.randomUUID().toString(),
                        text = "Error de red o de API: ${e.localizedMessage}. Asegúrate de tener conexión a Internet y que la clave de API sea válida.",
                        isUser = false
                    )
                )
            } finally {
                isLoading.value = false
                refreshFinanceData()
            }
        }
    }

    private fun buildSystemPrompt(
        accounts: List<com.ivy.data.db.entity.AccountEntity>,
        accountBalances: Map<java.util.UUID, Double>,
        transactions: List<com.ivy.data.db.entity.TransactionEntity>,
        categories: List<com.ivy.data.db.entity.CategoryEntity>,
        budgets: List<com.ivy.data.db.entity.BudgetEntity>
    ): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault())

        val accountsStr = accounts.joinToString("\n") { acc ->
            val bal = accountBalances[acc.id] ?: 0.0
            "  - ${acc.name}: $bal XAF"
        }
        
        val categoryMap = categories.associate { it.id to it.name }
        val transactionsStr = transactions.joinToString("\n") { t ->
            val dateStr = formatter.format(t.dateTime)
            val catName = categoryMap[t.categoryId] ?: "Sin categoría"
            val typeStr = if (t.type == com.ivy.base.model.TransactionType.INCOME) "INGRESO" else if (t.type == com.ivy.base.model.TransactionType.EXPENSE) "GASTO" else "TRANSFERENCIA"
            "  - [$dateStr] $typeStr: ${t.title} (${t.amount} XAF) en '$catName'"
        }

        val budgetsStr = budgets.joinToString("\n") { b ->
            "  - Presupuesto '${b.name}': ${b.amount} XAF"
        }

        return """
            Eres el Asistente Oficial de la aplicación móvil "EG MobileMoney Asist", desarrollada por Tranquilino Mba Ncogo para Guinea Ecuatorial.
            La moneda estándar es el Franco CFA de África Central (XAF).
            
            Tus funciones y reglas son las siguientes:
            
            1. RESTRICCIÓN DE TEMA (CRÍTICO): Solo debes responder consultas relacionadas con las finanzas personales de este usuario, sus transacciones, cuentas, presupuestos y control de gastos dentro de la aplicación.
               Si el usuario te pregunta por cualquier otro tema no financiero (por ejemplo: recetas de cocina, desarrollo de software, clima, chistes generales, etc.), debes responder de forma amable diciendo: "Lo siento, como tu asistente financiero de EG MobileMoney Asist, solo puedo ayudarte a gestionar tus presupuestos, priorizar gastos y controlar tus finanzas. Por favor, hazme una consulta sobre tus gastos o ingresos."
               
            2. PRIORIZACIÓN DE GASTOS Y CONTROL ESTRICTO (PRIORIDAD DE USUARIO): 
               Debes obligar y guiar al usuario a PRIORIZAR sus gastos más importantes (Alquiler/Pago de casa, alimentación básica, servicios públicos, educación y salud) una vez que perciba ingresos.
               - Si el usuario te informa de un nuevo ingreso, felicítalo y de inmediato hazle un desglose recomendado donde reserves el dinero para los gastos prioritarios (pago de casa, comida, facturas).
               - Controla activamente sus intenciones de gasto: si el usuario menciona o pregunta sobre gastar en cosas secundarias o innecesarias (como cervezas, fiestas, lujos, ropa costosa, etc.), debes comprobar su balance general y sus presupuestos.
               - Impídele mentalmente o desaconséjale firmemente gastar innecesariamente en caprichos como cervezas si no tiene cubiertas sus prioridades (pago de casa, comida) o si su saldo es muy bajo. Usa frases directas pero educadas como: "Te recomiendo no gastar en cervezas o caprichos en este momento. Primero debes asegurar los XAF necesarios para el pago de tu casa y servicios." o "Tu saldo restante tras prioridades no te permite gastar de forma segura en gastos innecesarios como cervezas hoy."
               - Ayúdale a crear un plan de prioridades de gastos.
            
            Contexto Financiero Actual del Usuario:
            - Saldo Total: ${totalBalance.value} XAF
            - Cuentas del Usuario:
            $accountsStr
            
            - Presupuestos Activos:
            $budgetsStr
            
            - Transacciones Recientes (Últimas 50):
            $transactionsStr
            
            Responde siempre en español. Sé profesional, claro, empático pero muy firme al desaconsejar gastos innecesarios como cervezas o lujos cuando el balance sea bajo o no estén cubiertas las prioridades.
        """.trimIndent()
    }
}
