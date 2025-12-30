package com.attri.WordOfDay.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attri.WordOfDay.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StartUpState {
    object Loading : StartUpState()
    object ApiKeyNeeded : StartUpState()
    object AppReady : StartUpState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _startUpState = MutableStateFlow<StartUpState>(StartUpState.Loading)
    val startUpState = _startUpState.asStateFlow()

    init {
        checkApiKey()
    }

    private fun checkApiKey() {
        viewModelScope.launch {
            val apiKey = preferencesRepository.apiKey.first()
            if (apiKey.isNullOrBlank()) {
                _startUpState.value = StartUpState.ApiKeyNeeded
            } else {
                _startUpState.value = StartUpState.AppReady
            }
        }
    }
    
    // Call this when the key is saved to navigate to the main app
    fun onApiKeySaved() {
        _startUpState.value = StartUpState.AppReady
    }
}
