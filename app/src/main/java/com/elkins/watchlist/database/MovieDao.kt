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

    @Query("SELECT * FROM movies_table ORDER BY title")
    fun getMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table ORDER BY following ASC, title")
    fun getMovies_SortFollowing_ASC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table ORDER BY following DESC, title")
    fun getMovies_SortFollowing_DESC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table ORDER BY releaseDate ASC")
    fun getMovies_SortReleaseDate_ASC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table ORDER BY releaseDate DESC")
    fun getMovies_SortReleaseDate_DESC(): LiveData<List<Movie>>


    @Query("SELECT * FROM movies_table WHERE id = :id")
    fun getMovie(id: Int): LiveData<Movie>

    @Query("UPDATE movies_table SET userScore=:newScore WHERE id=:id")
    fun updateMovieScore(newScore: Int, id: String)

    @Query("UPDATE movies_table SET following=:following WHERE id=:id")
    fun updateFollowingMovie(following: Boolean, id: String)

    @Query("DELETE FROM movies_table WHERE id = :id")
    fun deleteMovie(id: Int)

    @Query("DELETE FROM movies_table")
    fun clearDatabase()
}