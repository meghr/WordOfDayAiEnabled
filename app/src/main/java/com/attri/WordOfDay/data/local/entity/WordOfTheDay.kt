package com.attri.WordOfDay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "word_of_the_day")
@Serializable
data class WordOfTheDay(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String,
    @SerialName("hindi_meaning")
    val hindiMeaning: String,
    val definition: String = "", // Provide default value
    val synonym: String = "", // Provide default value
    val antonym: String = "", // Provide default value
    val sentences: List<String> = emptyList(), // Provide default value
    @SerialName("synonym_sentences")
    val synonymSentences: List<String> = emptyList(), // Provide default value
    @SerialName("antonym_sentences")
    val antonymSentences: List<String> = emptyList(), // Provide default value
    val dateFetched: Long = System.currentTimeMillis()
)
