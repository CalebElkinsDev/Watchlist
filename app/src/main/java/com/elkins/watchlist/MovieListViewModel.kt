package com.elkins.watchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elkins.watchlist.MovieRepository.MovieFilter
import com.elkins.watchlist.MovieRepository.SortType
import com.elkins.watchlist.model.Movie
import kotlinx.coroutines.launch

class MovieListViewModel(private val repository: MovieRepository) : ViewModel() {

    var currentSortType: SortType = SortType.TITLE
    var currentMovieFilter: MovieFilter = MovieFilter.ALL
    var sortAscending: Boolean = true

    // Live data list of movies from the local database
    private lateinit var _movies: LiveData<List<Movie>>
    val movies: LiveData<List<Movie>>
        get() = _movies

    // Update a movie if its rating or seen status has changed
    fun updateMovieScore(newScore: Int, id: String) {
        viewModelScope.launch {
            repository.updateMovieScore(newScore, id)
        }
    }

    fun updateHaveSeenMovie(following: Boolean, id: String) {
        viewModelScope.launch {
            repository.updateHaveSeenMovie(following, id)
        }
    }


    init {
        // Get filter, sort, and order from user preferences

        viewModelScope.launch {
            // Get movies saved to local database
            _movies = repository.getMovies(currentMovieFilter, currentSortType, sortAscending)
        }
    }
}

// ViewModelFactory for creating a MovieViewModel with a repository
class MovieListViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MovieListViewModel::class.java)) {
            return MovieListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}