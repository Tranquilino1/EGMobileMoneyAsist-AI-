package com.ivy.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IconScale
import com.ivy.design.l1_buildingBlocks.IvyIconScaled
import com.ivy.navigation.navigation
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.components.IvyToolbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BoxWithConstraintsScope.AiAssistantScreen() {
    val viewModel: AiAssistantViewModel = viewModel()
    val state = viewModel.uiState()
    val nav = navigation()

    var showBankModal by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.pure)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Toolbar
            IvyToolbar(
                onBack = { nav.onBackPressed() }
            ) {
                Spacer(Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "EG MobileMoney Asist",
                        style = UI.typo.b1.style(
                            color = UI.colors.pureInverse,
                            fontWeight = FontWeight.Black
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Asistente Financiero Inteligente",
                            style = UI.typo.c.style(
                                color = UI.colors.gray,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Bank Link Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(UI.colors.medium)
                        .clickable {
                            showBankModal = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IvyIconScaled(
                        icon = R.drawable.ic_vue_building_bank,
                        tint = Color(0xFF10B981),
                        iconScale = IconScale.M
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Clear Chat Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(UI.colors.medium)
                        .clickable {
                            viewModel.onEvent(AiAssistantEvent.ClearChat)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IvyIconScaled(
                        icon = R.drawable.ic_delete,
                        tint = UI.colors.pureInverse,
                        iconScale = IconScale.M
                    )
                }

                Spacer(Modifier.width(16.dp))
            }

            // Chat History Area
            val listState = rememberLazyListState()
            LaunchedEffect(state.messages.size) {
                if (state.messages.isNotEmpty()) {
                    listState.animateScrollToItem(state.messages.size - 1)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }

                items(state.messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }

                if (state.isLoading) {
                    item {
                        LoadingBubble()
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }

            // Input Row
            InputArea(
                inputText = state.inputText,
                isLoading = state.isLoading,
                onTextChange = { viewModel.onEvent(AiAssistantEvent.ChangeInput(it)) },
                onSend = { viewModel.onEvent(AiAssistantEvent.SendMessage) }
            )
        }

        // Animated Bank Link Overlay Modal
        AnimatedVisibility(
            visible = showBankModal,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            BankLinkOverlay(
                onDismiss = { showBankModal = false },
                onLink = { bank, balance, details ->
                    viewModel.onEvent(AiAssistantEvent.LinkBank(bank, balance, details))
                    showBankModal = false
                }
            )
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) {
        Color(0xFF10B981) // Emerald Green
    } else {
        UI.colors.medium // Obsidian panel / Slate gray
    }
    val textColor = if (isUser) Color.White else UI.colors.pureInverse

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(bubbleColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                style = UI.typo.b2.style(
                    color = textColor,
                    fontWeight = FontWeight.Medium
                ),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun LoadingBubble() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(UI.colors.medium)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color(0xFF10B981),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Conectando y sincronizando...",
                    style = UI.typo.c.style(
                        color = UI.colors.pureInverse,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun InputArea(
    inputText: String,
    isLoading: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(UI.colors.medium)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Input Box
        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(UI.colors.pure)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (inputText.isEmpty()) {
                Text(
                    text = "Pregunta sobre tu presupuesto o prioridades...",
                    style = UI.typo.b2.style(
                        color = UI.colors.gray,
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            BasicTextField(
                value = inputText,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = UI.typo.b2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(Color(0xFF10B981)),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSend()
                    }
                ),
                singleLine = true
            )
        }

        Spacer(Modifier.width(12.dp))

        // Send Button
        AnimatedVisibility(visible = inputText.isNotEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
                    .clickable {
                        onSend()
                    },
                contentAlignment = Alignment.Center
            ) {
                IvyIconScaled(
                    icon = R.drawable.ic_vue_main_send,
                    tint = Color.White,
                    iconScale = IconScale.M
                )
            }
        }
    }
}

@Composable
private fun BankLinkOverlay(
    onDismiss: () -> Unit,
    onLink: (bankName: String, balance: Double, details: String) -> Unit
) {
    val banks = listOf(
        "Ecobank", "Muni Dinero", "CCEIBank", "B-Mori",
        "Geleto", "Bettomax", "1xBet", "BGFIBank"
    )

    var selectedBank by remember { mutableStateOf("Ecobank") }
    var accountDetails by remember { mutableStateOf("") }
    var startingBalance by remember { mutableStateOf("50000") }
    var isVerifying by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { if (!isVerifying) onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(UI.colors.medium)
                .clickable(enabled = false) { }
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Vincular Cuenta Digital",
                style = UI.typo.b1.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Conecta de forma segura tus cuentas digitales de Guinea Ecuatorial",
                style = UI.typo.c.style(
                    color = UI.colors.gray,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(16.dp))

            // Bank selection grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(banks) { bank ->
                    val isSelected = bank == selectedBank
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF10B981) else UI.colors.pure)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else UI.colors.gray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = !isVerifying) { selectedBank = bank }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bank,
                            style = UI.typo.c.style(
                                color = if (isSelected) Color.White else UI.colors.pureInverse,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Details input (phone or account ID)
            val isPhoneInput = selectedBank in listOf("Muni Dinero", "B-Mori", "Geleto")
            Text(
                text = if (isPhoneInput) "Número de Teléfono (Mobile Money)" else "Número de Cuenta o ID de Usuario",
                style = UI.typo.c.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(UI.colors.pure)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (accountDetails.isEmpty()) {
                    Text(
                        text = if (isPhoneInput) "Ej: +240 222 123 456" else "Ej: 10023450912",
                        style = UI.typo.b2.style(color = UI.colors.gray)
                    )
                }
                BasicTextField(
                    value = accountDetails,
                    onValueChange = { if (!isVerifying) accountDetails = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = UI.typo.b2.style(color = UI.colors.pureInverse),
                    cursorBrush = SolidColor(Color(0xFF10B981)),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(16.dp))

            // Starting balance input
            Text(
                text = "Saldo Inicial (XAF)",
                style = UI.typo.c.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(UI.colors.pure)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (startingBalance.isEmpty()) {
                    Text(
                        text = "Ej: 50000",
                        style = UI.typo.b2.style(color = UI.colors.gray)
                    )
                }
                BasicTextField(
                    value = startingBalance,
                    onValueChange = { if (!isVerifying) startingBalance = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = UI.typo.b2.style(color = UI.colors.pureInverse),
                    cursorBrush = SolidColor(Color(0xFF10B981)),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(24.dp))

            // Connect button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(if (accountDetails.isNotEmpty() && startingBalance.isNotEmpty()) Color(0xFF10B981) else Color(0xFF10B981).copy(alpha = 0.5f))
                    .clickable(enabled = accountDetails.isNotEmpty() && startingBalance.isNotEmpty() && !isVerifying) {
                        isVerifying = true
                        scope.launch {
                            delay(2000) // simulated loading/syncing time
                            val balance = startingBalance.toDoubleOrNull() ?: 0.0
                            onLink(selectedBank, balance, accountDetails)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isVerifying) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Estableciendo Conexión Segura...",
                            style = UI.typo.b2.style(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    Text(
                        text = "Vincular y Sincronizar",
                        style = UI.typo.b2.style(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
