package com.attri.WordOfDay.data.repository

import com.attri.WordOfDay.data.local.dao.WordDao
import com.attri.WordOfDay.data.local.entity.WordOfTheDay
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GeminiRepository @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val wordDao: WordDao
) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchNewWordFromAI(): Result<WordOfTheDay> {
        return try {
            val prompt = """
                Return a JSON object for a trending/practical English word useful for competitive exams. 
                Fields required: { "word": "...", "hindi_meaning": "...", "definition": "...", "sentences": ["..."], "synonym": "...", "synonym_sentences": ["..."], "antonym": "...", "antonym_sentences": ["..."] }.
                Do not wrap the response in markdown code blocks. Just return the raw JSON string.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val responseText = response.text

            if (responseText != null) {
                // Sanitize the response in case it contains markdown code blocks
                val cleanJson = responseText.replace("```json", "").replace("```", "").trim()
                val wordOfTheDay = json.decodeFromString<WordOfTheDay>(cleanJson)
                
                // Save to Database
                wordDao.insertWord(wordOfTheDay)
                
                Result.success(wordOfTheDay)
            } else {
                Result.failure(Exception("Empty response from AI"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllWords(): Flow<List<WordOfTheDay>> {
        return wordDao.getAllWords()
    }
}
