package com.example.wanderlens.repository

import android.util.Base64
import com.example.wanderlens.data.model.JournalEntry
import com.example.wanderlens.data.remote.RetrofitClient
import com.example.wanderlens.data.remote.GeminiRequest
import com.example.wanderlens.data.remote.GenerationConfig
import com.example.wanderlens.data.remote.Content
import com.example.wanderlens.data.remote.Part
import com.example.wanderlens.data.remote.InlineData
import com.example.wanderlens.utils.Resource
import com.example.wanderlens.utils.ConfigProvider
import com.example.wanderlens.utils.FirebaseConfig
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.gson.Gson
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

data class GeminiJournalResponse(
    val title: String,
    val location: String,
    val country: String,
    val description: String,
    val funFacts: List<String>
)

class JournalRepository {

    private val firestore = FirebaseConfig.firestore
    private val auth = FirebaseConfig.auth

    fun getJournals(): Flow<Resource<List<JournalEntry>>> = flow {
        emit(Resource.Loading)
        try {
            val userId = auth.currentUser?.uid ?: "anonymous"
            val snapshot = firestore.collection("users").document(userId)
                .collection("journals")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val entries = snapshot.toObjects(JournalEntry::class.java)
            emit(Resource.Success(entries))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch journals"))
        }
    }

    fun getJournalById(journalId: String): Flow<Resource<JournalEntry>> = flow {
        emit(Resource.Loading)
        try {
            val userId = auth.currentUser?.uid ?: "anonymous"
            val document = firestore.collection("users").document(userId)
                .collection("journals").document(journalId)
                .get()
                .await()
            
            val entry = document.toObject(JournalEntry::class.java)
            if (entry != null) {
                emit(Resource.Success(entry))
            } else {
                emit(Resource.Error("Journal not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch journal detail"))
        }
    }

    suspend fun uploadToCloudinary(file: File): String {
        val timestamp = System.currentTimeMillis() / 1000
        val signatureStr = "timestamp=$timestamp${ConfigProvider.CLOUDINARY_API_SECRET}"
        val signature = sha1(signatureStr)

        val filePart = MultipartBody.Part.createFormData(
            "file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
        )

        val response = RetrofitClient.cloudinaryApi.uploadImage(
            cloudName = ConfigProvider.CLOUDINARY_CLOUD_NAME,
            file = filePart,
            timestamp = timestamp.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            apiKey = ConfigProvider.CLOUDINARY_API_KEY.toRequestBody("text/plain".toMediaTypeOrNull()),
            signature = signature.toRequestBody("text/plain".toMediaTypeOrNull())
        )
        return response.secure_url
    }

    private fun sha1(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun processAndSaveJournal(imageFile: File): Flow<Resource<JournalEntry>> = flow {
        emit(Resource.Loading)
        try {
            val imageUrl = uploadToCloudinary(imageFile)

            val compressedBytes = compressImage(imageFile)
            val base64Image = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)

            val prompt = """
                Analyze this travel photo and provide a JSON response with:
                {
                  "title": "A short poetic title for this trip",
                  "location": "Specific landmark or city name",
                  "country": "Country name",
                  "description": "A 2-sentence captivating travel story",
                  "funFacts": ["Fact 1", "Fact 2"]
                }
                Return ONLY the raw JSON, no markdown.
            """.trimIndent()

            val geminiRequest = GeminiRequest(
                contents = listOf(Content(parts = listOf(
                    Part(text = prompt),
                    Part(inlineData = InlineData(
                        mimeType = "image/jpeg",
                        data = base64Image
                    ))
                ))),
                generationConfig = GenerationConfig(
                    temperature = 0.5,
                    responseMimeType = "application/json"
                )
            )

            // Handle rate limiting with exponential backoff
            var geminiResponse: com.example.wanderlens.data.remote.GeminiResponse? = null
            var lastError: Exception? = null
            for (attempt in 1..3) {
                try {
                    geminiResponse = RetrofitClient.geminiApi.generateContent(
                        apiKey = ConfigProvider.GEMINI_API_KEY,
                        request = geminiRequest
                    )
                    break // Success
                } catch (e: retrofit2.HttpException) {
                    lastError = e
                    if (e.code() == 429 && attempt < 3) {
                        // Rate limited — wait and retry
                        kotlinx.coroutines.delay(22_000L * attempt)
                    } else {
                        throw e
                    }
                }
            }

            val rawJson = geminiResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            
            val jsonToParse = if (rawJson.contains("```json")) {
                rawJson.substringAfter("```json").substringBefore("```").trim()
            } else if (rawJson.contains("```")) {
                rawJson.substringAfter("```").substringBefore("```").trim()
            } else {
                rawJson.trim()
            }

            val aiData = try {
                Gson().fromJson(jsonToParse, GeminiJournalResponse::class.java)
            } catch (e: Exception) {
                null
            }
            
            val userId = auth.currentUser?.uid ?: "anonymous"
            val journalRef = firestore.collection("users").document(userId)
                .collection("journals").document()

            val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

            val newEntry = JournalEntry(
                id = journalRef.id,
                title = aiData?.title ?: "Travel Discovery",
                location = aiData?.location ?: "Unknown",
                country = aiData?.country ?: "World",
                dateText = dateFormat.format(Date()),
                imageUrl = imageUrl,
                description = aiData?.description ?: "Memory processed by WanderLens AI.",
                funFacts = aiData?.funFacts ?: emptyList(),
                timestamp = System.currentTimeMillis()
            )

            journalRef.set(newEntry).await()
            emit(Resource.Success(newEntry))

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Processing failed"))
        }
    }

    private fun compressImage(file: File): ByteArray {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val out = ByteArrayOutputStream()
        
        val maxDimension = 800
        val width = bitmap.width
        val height = bitmap.height
        
        val (newWidth, newHeight) = if (width > height) {
            if (width > maxDimension) {
                Pair(maxDimension, (height * maxDimension / width))
            } else Pair(width, height)
        } else {
            if (height > maxDimension) {
                Pair((width * maxDimension / height), maxDimension)
            } else Pair(width, height)
        }
        
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
        return out.toByteArray()
    }
}
