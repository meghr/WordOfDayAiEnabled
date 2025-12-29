package com.attri.WordOfDay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.attri.WordOfDay.data.local.entity.WordOfTheDay
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordOfTheDay)

    @Query("SELECT * FROM word_of_the_day ORDER BY dateFetched DESC LIMIT 1")
    fun getLastWord(): Flow<WordOfTheDay?>

    @Query("SELECT * FROM word_of_the_day ORDER BY dateFetched DESC")
    fun getAllWords(): Flow<List<WordOfTheDay>>
}