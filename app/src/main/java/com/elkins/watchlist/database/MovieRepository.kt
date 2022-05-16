package com.elkins.watchlist.database

import androidx.lifecycle.LiveData
import com.elkins.watchlist.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository class for fetching, updating and deleting movie information from local database.
 * Network calls are handled when specific searches are conducted. Otherwise, infromation comes
 * from the local database
 */
class MovieRepository(private val dataSource: MovieDao) {

    /**
     * Simple enum for movie sort types.
     *
     * @param sqlValue: The integer used in the Room Database query for advanced filtering.
     */
    enum class SortType(val sqlValue: Int) {
        TITLE(0),
        RELEASE_DATE(1),
        DATE_ADDED(2)}


    /**
     * Get all movies in the database according to the passed filters. The filters come from user
     * preferences and UI controls on the main page. Results are held in LiveData for observation.
     *
     * @param ascending: If true, sort in ascending order(according to sort type)
     * @param sortType: Sort by movie title, release date, or date added to list
     * @param showWatched: If true, show the movies in the "seen" list, otherwise show the
     *   "watchlist"(unseen movies)
     */
    fun getMovies(ascending: Boolean, sortType: SortType, showWatched: Boolean) : LiveData<List<Movie>> {
        return dataSource.getMovies(ascending,sortType. sqlValue, showWatched)
    }

    // Get the amount of movies in the user's "Watchlist"
    fun getWatchedMovieCount() : LiveData<Int?> {
        return dataSource.getWatchedMovieCount()
    }

    // Get the amount of movies in the user's "Seen" list
    fun getNotWatchedMovieCount() : LiveData<Int?> {
        return dataSource.getNotWatchedMovieCount()
    }

    /**
     * Return a [Movie] object from the database
     *
     * @param id: Unique Id used by both the database and the IMDB api.
     */
    suspend fun getMovie(id: String) : Movie? {
        return dataSource.getMovie(id)
    }

    /**
     * Used for adding a [Movie] from the network to the local database. Conversion from Network
     * model to Domain model occurs before call.
     */
    suspend fun addMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            dataSource.insert(movie)
        }
    }

    // Update the user's score for the movie with the given id
    suspend fun updateMovieScore(newScore: Float, id: String) {
        withContext(Dispatchers.IO) {
            dataSource.updateMovieScore(newScore, id)
        }
    }

    // Update the "haveSeen" variable of the movie with the given id
    suspend fun updateHaveSeenMovie(haveSeen: Boolean, id: String) {
        withContext(Dispatchers.IO) {
            dataSource.updateHaveSeenMovie(haveSeen, id)
        }
    }

    // Delete the movie with the given id from the local database
    suspend fun deleteMovie(id: String) {
        withContext(Dispatchers.IO) {
            dataSource.deleteMovie(id)
        }
    }
}