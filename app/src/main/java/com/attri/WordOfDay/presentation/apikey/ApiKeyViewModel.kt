package com.attri.WordOfDay.presentation.apikey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attri.WordOfDay.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiKeyViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val apiKey: StateFlow<String?> = preferencesRepository.apiKey.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            preferencesRepository.saveApiKey(key)
        }
    }
}
