package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SocialViewModel
import com.example.ui.components.AestheticImageContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: SocialViewModel,
    onNavigateBackToFeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var captionText by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf("sunset_beach") }
    var aiPrompt by remember { mutableStateOf("") }
    var showAiAssistSection by remember { mutableStateOf(false) }

    val aiLoading by viewModel.aiGenerationLoading.collectAsStateWithLifecycle()
    val aiResult by viewModel.aiGeneratedCaption.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    // Sync generated AI caption into the main caption text editor
    LaunchedEffect(aiResult) {
        if (aiResult.isNotBlank() && !aiResult.startsWith("Error")) {
            captionText = aiResult
        }
    }

    val themePresets = listOf(
        Pair("Sunset Beach", "sunset_beach"),
        Pair("Pasta Feast", "pasta_feast"),
        Pair("Cyber Mesh", "futuristic_ai"),
        Pair("Aurora Dream", "aurora_dream"),
        Pair("Oil Canvas", "oil_painting"),
        Pair("Forest Moss", "forest_moss"),
        Pair("Lavender Ice", "lavender_ice")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("create_post_screen")
    ) {
        Text(
            text = "Create Post",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // LIVE PREVIEW BLOCK
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .testTag("live_preview_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(16.dp),
            border = borderStroke(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Preview indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Style,
                        contentDescription = "Preview",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Preview",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Graphics Box
                AestheticImageContainer(
                    presetId = selectedPreset,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.22f)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "@${userProfile?.username ?: "sveta_dev"}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (locationText.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = locationText,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Text(
                    text = captionText.ifBlank { "Your caption will appear here..." },
                    fontSize = 12.5.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (captionText.isBlank()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // STYLING ROTATOR
        Text(
            text = "Select Background Style",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ScrollableThemeSelector(
            presets = themePresets,
            selectedId = selectedPreset,
            onSelect = { selectedPreset = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // LOCATION AND TEXT BLOCK
        Text(
            text = "Caption Content",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = captionText,
            onValueChange = { captionText = it },
            placeholder = { Text("What's on your mind? Add hashtags to categorize (#nature, #tech)...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("caption_input_field"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = locationText,
            onValueChange = { locationText = it },
            label = { Text("Add Location (Optional)") },
            placeholder = { Text("e.g. London, United Kingdom") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Location Pin") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("location_input_field"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // AI ASSIST / GEMINI BLOCK
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAiAssistSection = !showAiAssistSection },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "Gemini",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Caption Helper (Gemini)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = if (showAiAssistSection) "Hide" else "Show",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (showAiAssistSection) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Briefly enter what you want your post to convey (e.g. \"drinking warm hot chocolate during first autumn rain\"), and Gemini will compose a beautiful post draft with emojis!",
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = aiPrompt,
                            onValueChange = { aiPrompt = it },
                            placeholder = { Text("Write brief prompt...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_prompt_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        Button(
                            onClick = { viewModel.generateAiCaption(aiPrompt) },
                            enabled = aiPrompt.isNotBlank() && !aiLoading,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("ai_generate_button")
                        ) {
                            if (aiLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = "Generate",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (aiResult.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        if (aiResult.startsWith("Error")) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = aiResult,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        } else {
                            Text(
                                text = "Generated Preview:\n$aiResult",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SHARE POST BUTTON
        Button(
            onClick = {
                viewModel.createPost(captionText, selectedPreset, locationText)
                onNavigateBackToFeed()
            },
            enabled = captionText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("share_post_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Share Post",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun ScrollableThemeSelector(
    presets: List<Pair<String, String>>,
    selectedId: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { preset ->
            val isSelected = preset.second == selectedId
            val selectionColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.5f
                        )
                    )
                    .border(1.5.dp, selectionColor, RoundedCornerShape(12.dp))
                    .clickable { onSelect(preset.second) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = preset.first,
                    fontSize = 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun borderStroke(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)
