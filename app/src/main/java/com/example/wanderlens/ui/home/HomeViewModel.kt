package com.example.wanderlens.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlens.data.model.JournalEntry
import com.example.wanderlens.repository.JournalRepository
import com.example.wanderlens.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = JournalRepository()

    private val _journalsState = MutableStateFlow<Resource<List<JournalEntry>>>(Resource.Loading)
    val journalsState: StateFlow<Resource<List<JournalEntry>>> = _journalsState.asStateFlow()

    init {
        fetchJournals()
    }

    fun fetchJournals() {
        viewModelScope.launch {
            _journalsState.value = Resource.Loading

            repository.getJournals().collectLatest { resource ->
                _journalsState.value = resource
            }
        }
    }
}
