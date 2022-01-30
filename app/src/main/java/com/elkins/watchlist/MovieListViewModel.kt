package com.elkins.watchlist

import androidx.lifecycle.*
import com.elkins.watchlist.MovieRepository.SortType
import com.elkins.watchlist.model.Movie
import kotlinx.coroutines.launch

class MovieListViewModel(private val repository: MovieRepository) : ViewModel() {

    var currentSortType: SortType = SortType.TITLE
    var showWatchedMovies: Boolean = true
    var sortAscending: Boolean = true

    // Live data list of movies from the local database
    private lateinit var _movies: LiveData<List<Movie>>
    val movies: LiveData<List<Movie>>
        get() = _movies

    private var _listRefreshEvent = MutableLiveData(false)
    val listRefreshEvent: LiveData<Boolean>
        get() = _listRefreshEvent

    fun listRefreshEventHandled() {
        _listRefreshEvent.value = false
    }

    init {
        getMovies() // Get filter, sort, and order from user preferences
    }

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

    private fun getMovies() {
        viewModelScope.launch {
            // Get movies saved to local database
            _movies = repository.getMovies(showWatchedMovies, currentSortType, sortAscending)
            //_movies = repository.getMovies(currentMovieFilter)
            _listRefreshEvent.value = true
        }
    }

    fun updateFilter(showWatched: Boolean) {
        viewModelScope.launch {
            showWatchedMovies = showWatched
            getMovies()
        }
    }

    fun updateSortType(newSort: SortType) {
        currentSortType = newSort
        getMovies()
    }

    fun updateSortOrder(sortAscending: Boolean) {
        this.sortAscending = sortAscending
        getMovies()
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