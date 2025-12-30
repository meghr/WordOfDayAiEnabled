package com.attri.WordOfDay.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.attri.WordOfDay.presentation.apikey.ApiKeyViewModel

@Composable
fun SettingsScreen(
    apiKeyViewModel: ApiKeyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    // API Key state
    val currentApiKey by apiKeyViewModel.apiKey.collectAsState()
    var apiKeyInput by remember { mutableStateOf("") }
    var isApiKeyVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(currentApiKey) {
        apiKeyInput = currentApiKey ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // --- API Key Section ---
        Text(
            text = "Gemini API Key",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            label = { Text("API Key") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (isApiKeyVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (isApiKeyVisible) "Hide API Key" else "Show API Key"
                IconButton(onClick = { isApiKeyVisible = !isApiKeyVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { 
                apiKeyViewModel.saveApiKey(apiKeyInput)
                Toast.makeText(context, "API Key Saved!", Toast.LENGTH_SHORT).show()
            },
            enabled = apiKeyInput.isNotBlank() && apiKeyInput != currentApiKey,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update API Key")
        }
    }
}
