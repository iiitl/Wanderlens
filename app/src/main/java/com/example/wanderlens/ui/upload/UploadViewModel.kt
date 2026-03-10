package com.example.wanderlens.ui.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlens.data.model.JournalEntry
import com.example.wanderlens.repository.JournalRepository
import com.example.wanderlens.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class UploadViewModel : ViewModel() {

    private val repository = JournalRepository()

    private val _uploadState = MutableStateFlow<Resource<JournalEntry>?>(null)
    val uploadState: StateFlow<Resource<JournalEntry>?> = _uploadState.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<android.net.Uri?>(null)
    val selectedImageUri: StateFlow<android.net.Uri?> = _selectedImageUri.asStateFlow()

    fun setSelectedImageUri(uri: android.net.Uri?) {
        _selectedImageUri.value = uri
    }

    fun uploadJournal(imageFile: File) {
        viewModelScope.launch {
            repository.processAndSaveJournal(imageFile).collect {
                _uploadState.value = it
            }
        }
    }
    
    fun resetState() {
        _uploadState.value = null
    }
}
