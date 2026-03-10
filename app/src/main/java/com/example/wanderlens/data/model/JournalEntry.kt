package com.example.wanderlens.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JournalEntry(
    val id: String = "",
    val title: String = "",
    val location: String = "",
    val country: String = "",
    val dateText: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val funFacts: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable
