package com.elkins.watchlist

import androidx.lifecycle.LiveData
import com.elkins.watchlist.database.MovieDao
import com.elkins.watchlist.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Mediator between the user and the local database.
class MovieRepository(private val dataSource: MovieDao) {

    fun getMovies(): LiveData<List<Movie>> {
        return dataSource.getMovies()
    }

    suspend fun addMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            dataSource.insert(movie)
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            dataSource.clearDatabase()
        }
    }
}