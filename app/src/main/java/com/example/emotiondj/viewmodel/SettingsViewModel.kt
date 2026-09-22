package com.example.emotiondj.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("app_settings")
private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data.collectLatest { prefs ->
                _isDarkTheme.value = prefs[DARK_THEME_KEY] ?: true
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val newValue = !_isDarkTheme.value
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[DARK_THEME_KEY] = newValue
            }
            _isDarkTheme.value = newValue
        }
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context.applicationContext as Application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
