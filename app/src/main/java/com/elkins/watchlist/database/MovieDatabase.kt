package com.elkins.watchlist.database

import android.content.Context
import android.util.Log
import androidx.room.*
import com.elkins.watchlist.model.Movie
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val DATABASE_NAME = "movie_database"

@Database(entities = [Movie::class], version = 11, exportSchema = false)
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

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd")

    @TypeConverter
    fun fromDate(value: Date?): String {
        if(value != null) {
            return inputFormat.format(value)
        } else {
            return "N/A"
        }
    }

    @TypeConverter
    fun toDate(value: String?): Date? {
        try {
            return inputFormat.parse(value)
        } catch (e: ParseException) {
            Log.e("Parse", e.message.toString())
            return null
        }
    }
}