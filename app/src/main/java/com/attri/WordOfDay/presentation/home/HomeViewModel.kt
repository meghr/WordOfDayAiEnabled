package com.attri.WordOfDay.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attri.WordOfDay.data.local.entity.WordOfTheDay
import com.attri.WordOfDay.data.repository.GeminiRepository
import com.attri.WordOfDay.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    data class Success(val word: WordOfTheDay) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GeminiRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun fetchNewWord() {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val lastDate = preferencesRepository.lastClickDate.first()
            var currentCount = preferencesRepository.clickCount.first()

            if (lastDate != today) {
                // New day, reset count
                preferencesRepository.resetClickCount(today)
                currentCount = 0
            }

            if (currentCount >= 5) {
                _uiState.value = HomeUiState.Error("Daily limit of 5 words reached. Come back tomorrow!")
                return@launch
            }

            // Proceed with fetch
            _uiState.value = HomeUiState.Loading
            
            // Increment count immediately to prevent spamming
            preferencesRepository.incrementClickCount()
            preferencesRepository.updateLastClickDate(today)

            val result = repository.fetchNewWordFromAI()
            
            result.onSuccess { word ->
                _uiState.value = HomeUiState.Success(word)
            }.onFailure { exception ->
                _uiState.value = HomeUiState.Error(exception.message ?: "Unknown error occurred")
            }
        }
    }
}
