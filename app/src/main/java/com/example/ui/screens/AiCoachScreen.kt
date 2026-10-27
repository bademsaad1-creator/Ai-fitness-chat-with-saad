package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardBorder
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.Obsidian
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHighlight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPulse
import com.example.ui.theme.VividFlame
import com.example.ui.viewmodel.CoachMessage
import com.example.ui.viewmodel.FitnessViewModel

enum class CoachCategory(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val samplePrompts: List<String>
) {
    ALL(
        "All Topics",
        Icons.Default.AutoAwesome,
        NeonEmerald,
        listOf(
            "How to structure a 4-day workout split?",
            "What should I eat 1 hour before an intense lift?",
            "How to stay consistent on low-energy days?"
        )
    ),
    FORM(
        "Exercise Form",
        Icons.Default.FitnessCenter,
        ElectricCyan,
        listOf(
            "How to stop lower back rounding in deadlifts?",
            "Proper elbow angle for dumbbell bench press?",
            "Squat depth: How to fix hip shift and knee caving?",
            "Pull-ups: How to properly engage the lats?"
        )
    ),
    NUTRITION(
        "Nutrition Tips",
        Icons.Default.Restaurant,
        AmberGlow,
        listOf(
            "Optimal protein intake timing around training",
            "Best high-protein recovery meal under 600 kcal",
            "How much water should I drink during lifting?",
            "Creatine monohydrate: Timing and benefits"
        )
    ),
    MOTIVATION(
        "Motivation",
        Icons.Default.LocalFireDepartment,
        VividFlame,
        listOf(
            "I'm feeling unmotivated to workout today. Help!",
            "How to build iron mental discipline over motivation?",
            "Mindset cues to push through the final 2 hard reps",
            "How to break out of a fitness slump?"
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiCoachScreen(
    viewModel: FitnessViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.coachMessages.collectAsState()
    val isThinking by viewModel.isCoachThinking.collectAsState()
    val context = LocalContext.current

    var inputMessage by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CoachCategory.ALL) }
    val listState = rememberLazyListState()

    // Scroll to latest message smoothly
    LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceDark,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("coach_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NeonEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Coach Titan AI",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(NeonEmerald)
                                )
                            }
                            Text(
                                text = "Form • Nutrition • Motivation (Gemini 3.8)",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            viewModel.clearCoachChat()
                            Toast.makeText(context, "Chat cleared", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("clear_coach_chat_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = TextMuted
                        )
                    }
                }
            }

            // Category Filter Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CoachCategory.entries.toTypedArray()) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedCategory = cat }
                            .testTag("coach_cat_${cat.name.lowercase()}"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) cat.color.copy(alpha = 0.2f) else SurfaceDark,
                        border = BorderStroke(1.dp, if (isSelected) cat.color else CardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = null,
                                tint = if (isSelected) cat.color else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = cat.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) cat.color else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Suggested Prompt Chips (Dynamic based on selected category)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedCategory.samplePrompts) { promptText ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.sendCoachMessage(promptText, selectedCategory.label)
                            }
                            .testTag("suggested_prompt_chip"),
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceCard,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💬 $promptText",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Message Timeline
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    InteractiveChatBubble(
                        message = msg,
                        onCopyText = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Coach Advice", msg.text))
                            Toast.makeText(context, "Copied coach response", Toast.LENGTH_SHORT).show()
                        },
                        onSelectFollowUp = { followUpPrompt ->
                            viewModel.sendCoachMessage(followUpPrompt, selectedCategory.label)
                        }
                    )
                }

                if (isThinking && (messages.isEmpty() || !messages.last().isStreaming)) {
                    item {
                        ThinkingBubble()
                    }
                }
            }

            // Bottom Input Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceDark,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = { inputMessage = it },
                            placeholder = {
                                Text(
                                    when (selectedCategory) {
                                        CoachCategory.FORM -> "Ask about biomechanics & form cues..."
                                        CoachCategory.NUTRITION -> "Ask about macros, diet & fueling..."
                                        CoachCategory.MOTIVATION -> "Ask for mental drive & motivation..."
                                        CoachCategory.ALL -> "Ask Coach Titan anything..."
                                    },
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("coach_input_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = selectedCategory.color,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = SurfaceCard,
                                unfocusedContainerColor = SurfaceCard
                            ),
                            shape = RoundedCornerShape(16.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputMessage.isNotBlank()) {
                                    val text = inputMessage
                                    inputMessage = ""
                                    viewModel.sendCoachMessage(text, selectedCategory.label)
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(selectedCategory.color)
                                .testTag("coach_send_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Obsidian,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InteractiveChatBubble(
    message: CoachMessage,
    onCopyText: () -> Unit,
    onSelectFollowUp: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(NeonEmerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = NeonEmerald,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(modifier = Modifier.widthIn(max = 300.dp)) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 16.dp
                ),
                color = if (message.isUser) NeonEmerald else SurfaceCard,
                border = if (!message.isUser) BorderStroke(1.dp, CardBorder) else null
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Category badge for Coach
                    if (!message.isUser && message.category != "General" && message.category.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceDark,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = message.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Message text + streaming cursor
                    Text(
                        text = if (message.isStreaming && message.text.isNotBlank()) "${message.text} ▍"
                        else if (message.isStreaming && message.text.isBlank()) "Thinking..."
                        else message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (message.isUser) Obsidian else TextPrimary,
                        lineHeight = 22.sp
                    )

                    // Coach Message Footer: Copy Action
                    if (!message.isUser && !message.isStreaming && message.text.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SurfaceDark.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable(onClick = onCopyText)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = TextMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Suggested Follow-up Prompts
            if (!message.isUser && !message.isStreaming && message.suggestedFollowUps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Suggested follow-ups:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    message.suggestedFollowUps.forEach { followUp ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSelectFollowUp(followUp) }
                                .testTag("coach_follow_up_chip"),
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Text(
                                text = "➜ $followUp",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ElectricCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ThinkingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(NeonEmerald.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = NeonEmerald,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    color = NeonEmerald,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Coach Titan is analyzing with Gemini...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
