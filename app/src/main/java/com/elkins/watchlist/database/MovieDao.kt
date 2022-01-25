package com.elkins.watchlist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elkins.watchlist.model.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movie)

    @Query("SELECT * FROM movies_table")
    fun getMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE id = :id")
    fun getMovie(id: Int): LiveData<Movie>

    @Query("DELETE FROM movies_table WHERE id = :id")
    fun deleteMovie(id: Int)

    @Query("DELETE FROM movies_table")
    fun clearDatabase()
}