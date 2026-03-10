package com.example.wanderlens.utils

import android.content.Context
import com.example.wanderlens.BuildConfig
import java.util.Properties

object ConfigProvider {
    private val properties = Properties()
    private var isLoaded = false

    fun init(context: Context) {
        if (isLoaded) return
        try {
            isLoaded = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    
    val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY
    val CLOUDINARY_CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME
    val CLOUDINARY_API_KEY = BuildConfig.CLOUDINARY_API_KEY
    val CLOUDINARY_API_SECRET = BuildConfig.CLOUDINARY_API_SECRET
}
