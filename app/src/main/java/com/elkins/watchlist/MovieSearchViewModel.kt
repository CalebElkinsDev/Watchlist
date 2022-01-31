package com.elkins.watchlist.database

import android.util.Log
import androidx.lifecycle.*
import com.elkins.watchlist.MovieRepository
import com.elkins.watchlist.R
import com.elkins.watchlist.model.Movie
import com.elkins.watchlist.network.ImdbApi.retrofitService
import com.elkins.watchlist.network.MovieResponse
import com.elkins.watchlist.network.NetworkResponseHandler
import com.elkins.watchlist.network.SearchResponse
import com.elkins.watchlist.network.SearchResult
import com.elkins.watchlist.utility.Resource
import kotlinx.coroutines.launch
import java.util.*

class MovieSearchViewModel(private val repository: MovieRepository) : ViewModel() {

    // Live data list of movies found from search
    private var _results = MutableLiveData<Resource<SearchResponse>>()
    val results: LiveData<Resource<SearchResponse>>
        get() = _results

    // Live data used for notifying when an item has been removed from the list
    private var _lastRemovedIndex = MutableLiveData<Int>()
    val lastRemovedIndex: LiveData<Int>
        get() = _lastRemovedIndex

    private var _movieDetailsObject = MutableLiveData<Movie?>(null)
    val movieDetailsObject: LiveData<Movie?>
        get() = _movieDetailsObject

    private var _toastMessageEvent = MutableLiveData<Int?>(null) // Posts the string resource ID
    val toastMessageEvent: LiveData<Int?>
        get() = _toastMessageEvent


    fun searchForMovie(searchString: String) {
        viewModelScope.launch {
            try {
                val response: SearchResponse = retrofitService.searchForMovie(searchString, "feature")
                _results.value = NetworkResponseHandler.handleSuccess(response)
//                if(response.results!!.isEmpty()) {
//                    Log.d("Network", "In Try, empty results")
//                    _results.value = NetworkResponseHandler.handleEmptyResults()
//                } else {
//                    Log.d("Network", "In try, handle success")
//                    _results.value = NetworkResponseHandler.handleSuccess(response)
//                }
            } catch (e: Exception) {
                Log.e("Network", "${e.printStackTrace()}")
                _results.value = NetworkResponseHandler.handleException(e)
            }
        }
    }

    fun addMovieToRepository(searchResult: SearchResult, position: Int) {
        viewModelScope.launch {
            // Determine if movie is already in the user's watchlist
            val existingMovie = repository.getMovie(searchResult.id)
            if(existingMovie != null) {
                _toastMessageEvent.postValue(R.string.search_itemAlreadyAdded)
                return@launch
            }

            val response: MovieResponse = retrofitService.getMovieFromId(searchResult.id)
            Log.d("Network", "Response: = $response")
            val newMovie = response.toDataBaseModel() // Convert network object to database model
            newMovie.dateAdded = Calendar.getInstance().time // Set the dateAdded to be the current time
            repository.addMovie(newMovie) // Add the new movie to the repo after setting the time

            // Remove the added movie from the results and resubmit
            val editedSearchResponse = _results.value?.data
            editedSearchResponse?.results!!.remove(searchResult)
            _results.value = Resource.success(editedSearchResponse)

            _lastRemovedIndex.value = position // Notify observers that an item has been removed
        }
    }

    // Get a Movie object from the selected SearchResult object and update the LiveData to fire navigation from observer
    fun getMovieDetailsAndBeginNavigation(searchResult: SearchResult){
        viewModelScope.launch {
            val response: MovieResponse = retrofitService.getMovieFromId(searchResult.id)
            val newMovie = response.toDataBaseModel() // Convert network object to database model
            _movieDetailsObject.value = newMovie // Change to this notifies a navigation change
        }
    }

    // Nullify the LiveData once navigation is handled to prevent repeated observer notifies
    fun navigationToDetailsComplete() {
        _movieDetailsObject.value = null
    }

    // Nullify the LiveData once the toast message has been handled by observer
    fun toastMessageEventComplete() {
        _toastMessageEvent.value = null
    }
}

// ViewModelFactory for creating a MovieSearchViewModel with a repository
class MovieSearchViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MovieSearchViewModel::class.java)) {
            return MovieSearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}