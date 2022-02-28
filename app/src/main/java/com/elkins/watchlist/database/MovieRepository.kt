package com.elkins.watchlist.database

import androidx.lifecycle.LiveData
import com.elkins.watchlist.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Mediator between the user and the local database.
class MovieRepository(private val dataSource: MovieDao) {

    enum class SortType(val sqlValue: Int) {
        TITLE(0),
        RELEASE_DATE(1),
        DATE_ADDED(2)}


    fun getMovies(ascending: Boolean, sortType: SortType, showWatched: Boolean) : LiveData<List<Movie>> {
        return dataSource.getMovies(ascending,sortType. sqlValue, showWatched)
    }

    fun getWatchedMovieCount() : LiveData<Int?> {
        return dataSource.getWatchedMovieCount()
    }

    fun getNotWatchedMovieCount() : LiveData<Int?> {
        return dataSource.getNotWatchedMovieCount()
    }

    suspend fun getMovie(id: String) : Movie? {
        return dataSource.getMovie(id)
    }

    suspend fun addMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            dataSource.insert(movie)
        }
    }

    suspend fun updateMovieScore(newScore: Float, id: String) {
        withContext(Dispatchers.IO) {
            dataSource.updateMovieScore(newScore, id)
        }
    }

    suspend fun updateHaveSeenMovie(haveSeen: Boolean, id: String) {
        withContext(Dispatchers.IO) {
            dataSource.updateHaveSeenMovie(haveSeen, id)
        }
    }

    suspend fun deleteMovie(id: String) {
        withContext(Dispatchers.IO) {
            dataSource.deleteMovie(id)
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            dataSource.clearDatabase()
        }
    }
}