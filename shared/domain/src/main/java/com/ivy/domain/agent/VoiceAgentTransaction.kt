package com.ivy.domain.agent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceAgentTransaction(
    @SerialName("transaction_type")
    val transactionType: String,
    
    @SerialName("amount")
    val amount: Double,
    
    @SerialName("source_account_name")
    val sourceAccountName: String? = null,
    
    @SerialName("target_account_name")
    val targetAccountName: String? = null,
    
    @SerialName("category_name")
    val categoryName: String? = null,
    
    @SerialName("note")
    val note: String? = null
)
