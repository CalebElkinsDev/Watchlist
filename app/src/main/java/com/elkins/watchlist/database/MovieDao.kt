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

    // Old query for selecting either unwatched movies or unwatched AND watched
//    "WHERE CASE " +
//    "WHEN :showWatched = 0 THEN haveSeen = 0 " +
//    "WHEN :showWatched = 1 THEN haveSeen = 1 OR haveSeen = 0 END " +

    @Query("SELECT * FROM movies_table " +
            "WHERE haveSeen = :showWatched " +
            "ORDER BY haveSeen, " +
            "CASE WHEN :sortType = 0 AND :sortAscending = true THEN title END ASC, " +
            "CASE WHEN :sortType = 0 AND :sortAscending = false THEN title END DESC, " +
            "CASE WHEN :sortType = 1 AND :sortAscending = true THEN releaseDate END ASC, " +
            "CASE WHEN :sortType = 1 AND :sortAscending = false THEN releaseDate END DESC, " +
            "CASE WHEN :sortType = 2 AND :sortAscending = true THEN dateAdded END ASC, " +
            "CASE WHEN :sortType = 2 AND :sortAscending = false THEN dateAdded END DESC")
    fun getMovies(sortAscending: Boolean, sortType: Int = 0, showWatched: Boolean) : LiveData<List<Movie>>

    @Query("SELECT COUNT(id) FROM movies_table WHERE haveSeen = 1")
    fun getWatchedMovieCount(): LiveData<Int?>

    @Query("SELECT COUNT(id) FROM movies_table WHERE haveSeen = 0")
    fun getNotWatchedMovieCount(): LiveData<Int?>

    @Query("SELECT * FROM movies_table WHERE id = :id")
    suspend fun getMovie(id: String): Movie?

    @Query("UPDATE movies_table SET userScore=:newScore WHERE id=:id")
    fun updateMovieScore(newScore: Int, id: String)

    @Query("UPDATE movies_table SET haveSeen=:haveSeen WHERE id=:id")
    fun updateHaveSeenMovie(haveSeen: Boolean, id: String)

    @Query("DELETE FROM movies_table WHERE id = :id")
    fun deleteMovie(id: String)

    @Query("DELETE FROM movies_table")
    fun clearDatabase()
}