package com.example.emotiondj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotiondj.utils.MoodHistoryManager
import com.example.emotiondj.utils.MoodRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val historyManager = MoodHistoryManager(application)
    private val _moods = MutableStateFlow<List<MoodRecord>>(emptyList())
    val moods = _moods.asStateFlow()

    init {
        loadMoods()
    }

    private fun loadMoods() {
        viewModelScope.launch {
            historyManager.getAllMoods().collect { moodList ->
                _moods.value = moodList.reversed() // newest first
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyManager.clearHistory()
        }
    }
}
