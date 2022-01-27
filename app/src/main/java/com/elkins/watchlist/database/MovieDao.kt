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


    /* Queries for getting all movies */
    @Query("SELECT * FROM movies_table ORDER BY title")
    fun getAllMovies_Title_ASC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table ORDER BY title DESC")
    fun getAllMovies_Title_DESC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table ORDER BY releaseDate")
    fun getAllMovies_ReleaseDate_ASC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table ORDER BY releaseDate DESC")
    fun getAllMovies_ReleaseDate_DESC(): LiveData<List<Movie>>


    /* Queries for getting unseen movies */
    @Query("SELECT * FROM movies_table WHERE haveSeen = 0 ORDER BY title")
    fun getUnseenMovies_Title_ASC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE haveSeen = 0 ORDER BY title DESC")
    fun getUnseenMovies_Title_DESC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE haveSeen = 0 ORDER BY releaseDate")
    fun getUnseenMovies_ReleaseDate_ASC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE haveSeen = 0 ORDER BY releaseDate DESC")
    fun getUnseenMovies_ReleaseDate_DESC(): LiveData<List<Movie>>


    /* Queries for getting seen movies */
    @Query("SELECT * FROM movies_table WHERE haveSeen = 1 ORDER BY title")
    fun getSeenMovies_Title_ASC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE haveSeen = 1 ORDER BY title DESC")
    fun getSeenMovies_Title_DESC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE haveSeen = 1 ORDER BY releaseDate")
    fun getSeenMovies_ReleaseDate_ASC(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE haveSeen = 1 ORDER BY releaseDate DESC")
    fun getSeenMovies_ReleaseDate_DESC(): LiveData<List<Movie>>




    @Query("SELECT * FROM movies_table WHERE id = :id")
    fun getMovie(id: Int): LiveData<Movie>

    @Query("UPDATE movies_table SET userScore=:newScore WHERE id=:id")
    fun updateMovieScore(newScore: Int, id: String)

    @Query("UPDATE movies_table SET haveSeen=:haveSeen WHERE id=:id")
    fun updateHaveSeenMovie(haveSeen: Boolean, id: String)

    @Query("DELETE FROM movies_table WHERE id = :id")
    fun deleteMovie(id: Int)

    @Query("DELETE FROM movies_table")
    fun clearDatabase()
}