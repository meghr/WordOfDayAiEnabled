package com.attri.WordOfDay.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.attri.WordOfDay.data.local.converters.Converters
import com.attri.WordOfDay.data.local.dao.WordDao
import com.attri.WordOfDay.data.local.entity.WordOfTheDay

@Database(entities = [WordOfTheDay::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}