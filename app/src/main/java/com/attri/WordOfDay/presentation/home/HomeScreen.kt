package com.attri.WordOfDay.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attri.WordOfDay.data.local.entity.WordOfTheDay
import com.attri.WordOfDay.util.TextToSpeechManager
import com.attri.WordOfDay.util.shareWord

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val remainingCount by viewModel.remainingCount.collectAsState() // Observe remaining count
    val context = LocalContext.current
    
    // Initialize TTS Manager
    val ttsManager = remember { TextToSpeechManager(context) }
    
    // Clean up TTS when screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Header Row with Title and Share Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VocabDaily",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Daily Limit: $remainingCount left",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (uiState is HomeUiState.Success) {
                IconButton(
                    onClick = {
                        val word = (uiState as HomeUiState.Success).word
                        shareWord(context, word)
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        }

        // 2. Main Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator()
                is HomeUiState.Success -> {
                    WordCardContent(
                        wordOfTheDay = state.word,
                        onPlayAudio = { text -> ttsManager.speak(text) }
                    )
                }
                is HomeUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchNewWord() }) {
                            Text("Retry")
                        }
                    }
                }
                HomeUiState.Idle -> {
                    LaunchedEffect(Unit) {
                        viewModel.fetchNewWord()
                    }
                }
            }
        }

        // 3. Footer Action Button (Learn New Word)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.fetchNewWord() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = uiState !is HomeUiState.Loading && remainingCount > 0
        ) {
            if (uiState is HomeUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                if (remainingCount > 0) {
                    Text("Learn New Word ($remainingCount left)")
                } else {
                    Text("Daily Limit Reached")
                }
            }
        }
    }
}

@Composable
fun WordCardContent(
    wordOfTheDay: WordOfTheDay,
    onPlayAudio: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title Row with Word and Speaker Icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = wordOfTheDay.word,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { onPlayAudio(wordOfTheDay.word) },
                            modifier = Modifier
                                .size(36.dp)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Listen",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hindi: ${wordOfTheDay.hindiMeaning}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF009688) // Teal Color
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (wordOfTheDay.marathiMeaning.isNotEmpty()) {
                        Text(
                            text = "Marathi: ${wordOfTheDay.marathiMeaning}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF673AB7) // Deep Purple (nicer than Pink)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = wordOfTheDay.definition,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            SectionTitle("Example Sentences")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    wordOfTheDay.sentences.forEach { sentence ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.clickable { onPlayAudio(sentence) }
                        ) {
                            Text("• ", style = MaterialTheme.typography.bodyMedium)
                            Text(sentence, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        if (wordOfTheDay.marathiSentences.isNotEmpty()) {
            item {
                SectionTitle("Marathi Sentences")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        wordOfTheDay.marathiSentences.forEach { sentence ->
                            Row(
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", style = MaterialTheme.typography.bodyMedium)
                                Text(sentence, style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        item {
            ExpandableSection(
                title = "Synonyms",
                content = wordOfTheDay.synonym,
                sentences = wordOfTheDay.synonymSentences,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onPlayAudio = onPlayAudio
            )
        }

        item {
            ExpandableSection(
                title = "Antonyms",
                content = wordOfTheDay.antonym,
                sentences = wordOfTheDay.antonymSentences,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                onPlayAudio = onPlayAudio
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ExpandableSection(
    title: String,
    content: String,
    sentences: List<String>,
    containerColor: Color,
    contentColor: Color,
    onPlayAudio: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationState)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = content,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(
                            onClick = { onPlayAudio(content) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Listen",
                                tint = contentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = contentColor.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    sentences.forEach { sentence ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.clickable { onPlayAudio(sentence) }
                        ) {
                            Text("• ", style = MaterialTheme.typography.bodyMedium)
                            Text(sentence, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}
