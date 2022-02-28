package com.elkins.watchlist.database

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.room.*
import com.elkins.watchlist.model.Movie
import java.text.SimpleDateFormat
import java.util.*

private const val DATABASE_NAME = "movie_database"

@Database(entities = [Movie::class], version = 12, exportSchema = false)
@TypeConverters(Converters::class)
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

class Converters {

    @SuppressLint("SimpleDateFormat") // Locale not used with API yet
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd")

    @TypeConverter
    fun fromDate(value: Date?): String {
        return if(value != null) {
            inputFormat.format(value)
        } else {
            "N/A"
        }
    }

    @TypeConverter
    fun toDate(value: String?): Date? {
        return try {
            inputFormat.parse(value)
        } catch (e: Exception) {
            Log.e("Parse Date", "${e.printStackTrace()}")
            null
        }
    }
}