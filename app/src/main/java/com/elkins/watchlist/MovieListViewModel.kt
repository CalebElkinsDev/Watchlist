package com.elkins.watchlist

import androidx.lifecycle.*
import com.elkins.watchlist.MovieRepository.SortType
import com.elkins.watchlist.model.Movie
import com.elkins.watchlist.utility.MovieLayoutType
import kotlinx.coroutines.launch

class MovieListViewModel(private val repository: MovieRepository) : ViewModel() {

    private class SortOptions(var sortAscending: Boolean,
                              var sortType: SortType,
                              var showWatched: Boolean)

    // Live data observed by fragment for updating thw recycler view
    private val _movies: LiveData<List<Movie>>
    val movies: LiveData<List<Movie>>
        get() = _movies

    private var _currentListType = MutableLiveData(MovieLayoutType.FULL)
    val currentListType: LiveData<MovieLayoutType>
        get() = _currentListType

    private lateinit var _watchedMoviesCount: LiveData<Int?>
    val watchedMoviesCount: LiveData<Int?>
        get() = _watchedMoviesCount

    private lateinit var _notWatchedMoviesCount: LiveData<Int?>
    val notWatchedMoviesCount: LiveData<Int?>
        get() = _notWatchedMoviesCount

    private var sortAscending = true
    private var showWatched = true
    private var sortType = SortType.TITLE

    /* Live data containing sort and filter options. Used for mapping the observed _movies LiveData
     * with filtered data from the repository */
    private var sortOptions = MutableLiveData(SortOptions(sortAscending, sortType, showWatched))

    init {
        _movies = Transformations.switchMap(sortOptions) {
            repository.getMovies(it.sortAscending, it.sortType, it.showWatched)
        }
        viewModelScope.launch {
            _watchedMoviesCount = repository.getWatchedMovieCount()
            _notWatchedMoviesCount = repository.getNotWatchedMovieCount()
        }
    }


    // Called whenever any of the sort/filter options are changed to trigger database query
    private fun updateSortOptions() {
        sortOptions.postValue(SortOptions(sortAscending, sortType, showWatched))
    }

    /* Public functions for updating sorting/filtering options. Each one triggers a database query */
    fun setSortAscending(ascending: Boolean) {
        sortAscending = ascending
        updateSortOptions()
    }

    fun setSortType(type: SortType) {
        sortType = type
        updateSortOptions()
    }

    fun setShowWatched(show: Boolean) {
        showWatched = show
        updateSortOptions()
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

    fun cycleListType() {
        _currentListType.value = when(_currentListType.value) {
            MovieLayoutType.FULL -> MovieLayoutType.SIMPLE
            MovieLayoutType.SIMPLE -> MovieLayoutType.POSTER
            MovieLayoutType.POSTER -> MovieLayoutType.FULL
            else -> MovieLayoutType.FULL
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