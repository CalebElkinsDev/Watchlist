package com.elkins.watchlist.list_fragment

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.elkins.watchlist.database.MovieRepository
import com.elkins.watchlist.database.MovieRepository.SortType
import com.elkins.watchlist.model.Movie
import com.elkins.watchlist.utility.MovieLayoutType
import kotlinx.coroutines.launch

private const val SHARED_PREF = "com.elkins.watchlist.sharedPrefs"
private const val PREF_SORT_ASCENDING = "sortAscending"
private const val PREF_SHOW_WATCHED = "showWatched"
private const val PREF_SORT_TYPE = "sortType"
private const val PREF_LIST_TYPE = "listType"


/** Main ViewModel implementation for displaying and updating the list of movies to the user */
class MovieListViewModel(private val repository: MovieRepository, application: Application) :
    AndroidViewModel(application) {

    /**
     * Inner class utilized for passing a suite of list filter options
     *
     * @param sortAscending: Orders movies ascending if true based on the sort type
     * @param sortType: Determines what criteria to order movies by(e.g., Title)
     * @param showWatched: Seperates movies into 2 lists: "Watchlist" and "Seen"
     */
    private class SortOptions(var sortAscending: Boolean,
                              var sortType: SortType,
                              var showWatched: Boolean)


    // Live data observed by fragment for updating the recycler view's current list
    private val _movies: LiveData<List<Movie>>
    val movies: LiveData<List<Movie>>
        get() = _movies

    // Filter LiveData for changing the layout type of the movies list
    private var _currentListType = MutableLiveData(MovieLayoutType.FULL)
    val currentListType: LiveData<MovieLayoutType>
        get() = _currentListType

    // LiveData containing the current number of movies in the user's "Seen" list
    private lateinit var _watchedMoviesCount: LiveData<Int?>
    val watchedMoviesCount: LiveData<Int?>
        get() = _watchedMoviesCount

    // LiveData containing the current number of movies in the user's "Watchlist"
    private lateinit var _notWatchedMoviesCount: LiveData<Int?>
    val notWatchedMoviesCount: LiveData<Int?>
        get() = _notWatchedMoviesCount


    /** The ViewModel's current filter options. Setters available for modifcation in [MovieListFragment] */
    private var sortAscending = true
    fun getSortAscending(): Boolean = sortAscending
    private var showWatched = false
    fun getShowWatched(): Boolean = showWatched
    private var sortType = SortType.TITLE
    fun getSortType(): SortType = sortType


    /**
     * Live data containing sort and filter options. Used for mapping the observed _movies LiveData
     * with filtered data from the repository
     */
    private var sortOptions = MutableLiveData(SortOptions(sortAscending, sortType, showWatched))


    init {
        /* Get sort and display values from shared preferences */
        val sharedPref = application.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        sortAscending = sharedPref.getBoolean(PREF_SORT_ASCENDING, true)
        showWatched = sharedPref.getBoolean(PREF_SHOW_WATCHED, false)
        val sortTypeString = sharedPref.getString(PREF_SORT_TYPE, "TITLE")?: "TITLE"
        sortType = SortType.valueOf(sortTypeString)
        updateSortOptions()

        val listTypeString = sharedPref.getString(PREF_LIST_TYPE, "FULL")?: "FULL"
        _currentListType.postValue(MovieLayoutType.valueOf(listTypeString))

        /** Transform the repostirory's movie list based on this viewmodel's current [SortOptions] */
        _movies = Transformations.switchMap(sortOptions) {
            repository.getMovies(it.sortAscending, it.sortType, it.showWatched)
        }

        // Get the number of movies in the two lists from the repository
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
        updateSharedPreferences()
    }

    fun setSortType(type: SortType) {
        sortType = type
        updateSortOptions()
        updateSharedPreferences()
    }

    fun setShowWatched(show: Boolean) {
        showWatched = show
        updateSortOptions()
        updateSharedPreferences()
    }

    // Update a movie if its user rating has changed
    fun updateMovieScore(newScore: Float, id: String) {
        viewModelScope.launch {
            repository.updateMovieScore(newScore, id)
        }
    }

    // Update a movie if its seen status has changed
    fun updateHaveSeenMovie(following: Boolean, id: String) {
        viewModelScope.launch {
            repository.updateHaveSeenMovie(following, id)
        }
    }

    // Used for cyclically changing between the MovieLayoutTypes.
    fun cycleListType() {
        _currentListType.value = when(_currentListType.value) {
            MovieLayoutType.FULL -> MovieLayoutType.SIMPLE
            MovieLayoutType.SIMPLE -> MovieLayoutType.POSTER
            MovieLayoutType.POSTER -> MovieLayoutType.FULL
            else -> MovieLayoutType.FULL
        }
        updateSharedPreferences()
    }

    private fun updateSharedPreferences() {
        val sharedPref = getApplication<Application>().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(PREF_SORT_ASCENDING, sortAscending)
            putBoolean(PREF_SHOW_WATCHED, showWatched)
            putString(PREF_SORT_TYPE, sortType.toString())
            putString(PREF_LIST_TYPE, _currentListType.value.toString())
            apply()
        }
    }
}

// ViewModelFactory for creating a MovieViewModel with a repository
class MovieListViewModelFactory(private val repository: MovieRepository,
                                private val application: Application)
    : ViewModelProvider.Factory { override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MovieListViewModel::class.java)) {
            return MovieListViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}