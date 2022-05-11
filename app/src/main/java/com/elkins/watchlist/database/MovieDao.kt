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

    /**
     * Main query for populating the watch list and seen list.
     *
     * Queries the database for all movies that fit the user's current filters and preferences.
     * LiveData is returned and observed for dynamic changes to RecyclerViews when filters change.
     *
     * Cannot Resolve Symbol errors for 'true' and 'false' after updating dependencies,
     * but query still works correctly */
    @Suppress("AndroidUnresolvedRoomSqlReference", "AndroidUnresolvedRoomSqlReference",
        "AndroidUnresolvedRoomSqlReference", "AndroidUnresolvedRoomSqlReference",
        "AndroidUnresolvedRoomSqlReference")
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
    fun updateMovieScore(newScore: Float, id: String)

    @Query("UPDATE movies_table SET haveSeen=:haveSeen WHERE id=:id")
    fun updateHaveSeenMovie(haveSeen: Boolean, id: String)

    @Query("DELETE FROM movies_table WHERE id = :id")
    fun deleteMovie(id: String)

    @Query("DELETE FROM movies_table")
    fun clearDatabase()
}