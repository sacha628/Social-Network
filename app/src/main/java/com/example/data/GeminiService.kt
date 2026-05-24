package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateCaption(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "API Key is missing or default placeholder!")
            return@withContext "Error: Please configure GEMINI_API_KEY in the Secrets panel.\n\n(Fallback caption: Here is an amazing post draft! 🌟✨ #awesome)"
        }

        try {
            val root = JSONObject()
            val contents = JSONArray()
            val contentObj = JSONObject()
            val parts = JSONArray()
            val partObj = JSONObject()
            
            // Give system instructions directly inside the prompt to style the output perfectly
            partObj.put("text", "You are an expert Social Media manager. Generate an engaging, trendy, high-quality post caption (with appropriate emojis and 2-3 tags) based on this prompt: '$prompt'. Make it concise, catchy, and exciting. Return only the caption.")
            
            parts.put(partObj)
            contentObj.put("parts", parts)
            contents.put(contentObj)
            root.put("contents", contents)

            val requestBody = root.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "Error: API call failed with code ${response.code}"
                }
                val bodyString = response.body?.string() ?: return@withContext "Error: Empty response body"
                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.getJSONArray("candidates")
                val firstCandidate = candidates.getJSONObject(0)
                val retContent = firstCandidate.getJSONObject("content")
                val retParts = retContent.getJSONArray("parts")
                val firstPart = retParts.getJSONObject(0)
                firstPart.getString("text").trim()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini call failed", e)
            "Error: ${e.localizedMessage ?: "Failed to reach Gemini. Check your network or API key."}"
        }
    }
}
