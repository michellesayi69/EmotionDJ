package com.example.emotiondj.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import java.util.Date

@Database(entities = [MoodEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MoodHistoryDatabase : RoomDatabase() {
    abstract fun moodHistoryDao(): MoodHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: MoodHistoryDatabase? = null

        fun getDatabase(context: Context): MoodHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodHistoryDatabase::class.java,
                    "mood_history_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}