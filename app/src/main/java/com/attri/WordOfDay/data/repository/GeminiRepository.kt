package com.attri.WordOfDay.data.repository

import com.attri.WordOfDay.data.local.dao.WordDao
import com.attri.WordOfDay.data.local.entity.WordOfTheDay
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GeminiRepository @Inject constructor(
    private val wordDao: WordDao,
    private val preferencesRepository: PreferencesRepository
) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchNewWordFromAI(): Result<WordOfTheDay> {
        val apiKey = preferencesRepository.apiKey.first()
        if (apiKey.isNullOrBlank()) {
            return Result.failure(Exception("API Key not found. Please set it in the app settings."))
        }

        val generativeModel = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey
        )

        return try {
            // Fetch past words to avoid repetition
            val pastWords = wordDao.getAllWords().first().joinToString(", ") { it.word }

            val prompt = """
                Return a JSON object for a trending/practical English word useful for competitive exams. 
                
                Constraints:
                1. The word MUST NOT be one of the following: [$pastWords].
                2. Choose a unique, sophisticated word (GRE/GMAT/IELTS level).
                
                Fields required: 
                { 
                  "word": "...", 
                  "hindi_meaning": "...", 
                  "definition": "...", 
                  "sentences": ["..."], 
                  "synonym": "...", 
                  "synonym_sentences": ["..."], 
                  "antonym": "...", 
                  "antonym_sentences": ["..."],
                  "marathi_meaning": "...",
                  "marathi_sentences": ["..."]
                }
                
                For 'marathi_meaning', provide the Marathi translation.
                For 'marathi_sentences', provide 2-3 example sentences in Marathi usage.

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
