package com.elkins.watchlist

import androidx.lifecycle.LiveData
import com.elkins.watchlist.database.MovieDao
import com.elkins.watchlist.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Mediator between the user and the local database.
class MovieRepository(private val dataSource: MovieDao) {

    enum class SortType { TITLE, RELEASE_DATE, DATE_ADDED}
    enum class MovieFilter { ALL, UNSEEN, SEEN}

    fun getMovies(filter: MovieFilter = MovieFilter.ALL,
                  sortType: SortType = SortType.TITLE,
                  sortAscending: Boolean = true): LiveData<List<Movie>> {

        return when(filter) {
            MovieFilter.ALL -> when(sortType) {
                SortType.TITLE -> if(sortAscending) dataSource.getAllMovies_Title_ASC()
                                            else dataSource.getAllMovies_Title_DESC()
                SortType.RELEASE_DATE -> if(sortAscending) dataSource.getAllMovies_ReleaseDate_ASC()
                                            else dataSource.getAllMovies_ReleaseDate_DESC()
                SortType.DATE_ADDED -> if(sortAscending) dataSource.getAllMovies_DateAdded_ASC()
                                             else dataSource.getAllMovies_DateAdded_DESC()
            }

            MovieFilter.UNSEEN -> when(sortType) {
                SortType.TITLE -> if(sortAscending) dataSource.getUnseenMovies_Title_ASC()
                                            else dataSource.getUnseenMovies_Title_DESC()
                SortType.RELEASE_DATE -> if(sortAscending) dataSource.getUnseenMovies_ReleaseDate_ASC()
                                            else dataSource.getUnseenMovies_ReleaseDate_DESC()
                SortType.DATE_ADDED -> if(sortAscending) dataSource.getUnseenMovies_DateAdded_ASC()
                                            else dataSource.getUnseenMovies_DateAdded_DESC()
            }

            MovieFilter.SEEN -> when(sortType) {
                SortType.TITLE -> if (sortAscending) dataSource.getSeenMovies_Title_ASC()
                                            else dataSource.getSeenMovies_Title_DESC()
                SortType.RELEASE_DATE -> if (sortAscending) dataSource.getSeenMovies_ReleaseDate_ASC()
                                            else dataSource.getSeenMovies_ReleaseDate_DESC()
                SortType.DATE_ADDED -> if(sortAscending) dataSource.getSeenMovies_DateAdded_ASC()
                                            else dataSource.getSeenMovies_DateAdded_DESC()
            }
        }
    }

    suspend fun addMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            dataSource.insert(movie)
        }
    }

    suspend fun updateMovieScore(newScore: Int, id: String) {
        withContext(Dispatchers.IO) {
            dataSource.updateMovieScore(newScore, id)
        }
    }

    suspend fun updateHaveSeenMovie(haveSeen: Boolean, id: String) {
        withContext(Dispatchers.IO) {
            dataSource.updateHaveSeenMovie(haveSeen, id)
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            dataSource.clearDatabase()
        }
    }
}