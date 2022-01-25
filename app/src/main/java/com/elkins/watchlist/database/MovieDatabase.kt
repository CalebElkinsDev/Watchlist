package com.elkins.watchlist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.elkins.watchlist.model.Movie

private const val DATABASE_NAME = "movie_database"

@Database(entities = [Movie::class], version = 5, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {

    abstract val movieDao: MovieDao

    companion object {

        @Volatile
        private var INSTANCE: MovieDatabase? = null

        // Get or create the singleton of the database
        fun getInstance(context: Context): MovieDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MovieDatabase::class.java,
                        DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}