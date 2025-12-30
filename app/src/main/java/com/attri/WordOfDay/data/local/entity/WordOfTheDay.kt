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
    val definition: String = "", 
    val synonym: String = "", 
    val antonym: String = "", 
    val sentences: List<String> = emptyList(), 
    @SerialName("synonym_sentences")
    val synonymSentences: List<String> = emptyList(), 
    @SerialName("antonym_sentences")
    val antonymSentences: List<String> = emptyList(),
    @SerialName("marathi_meaning")
    val marathiMeaning: String = "",
    @SerialName("marathi_sentences")
    val marathiSentences: List<String> = emptyList(),
    val dateFetched: Long = System.currentTimeMillis()
)
