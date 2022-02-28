package com.elkins.watchlist.search_fragment

import android.util.Log
import androidx.lifecycle.*
import com.elkins.watchlist.R
import com.elkins.watchlist.database.MovieRepository
import com.elkins.watchlist.model.Movie
import com.elkins.watchlist.network.*
import com.elkins.watchlist.network.ImdbApi.retrofitService
import com.elkins.watchlist.utility.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException
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

    // Used to trigger events when a movie is added to the local database
    private val _movieAddedToDatabase = MutableLiveData<Movie?>(null)
    val movieAddedToDatabase: LiveData<Movie?>
        get() = _movieAddedToDatabase


    fun searchForMovie(searchString: String) {
        viewModelScope.launch {
            try {
                ImdbApi.cancelRequests() // Cancel existing requests

                val response: SearchResponse = retrofitService.searchForMovie(searchString, "feature")
                _results.value = NetworkResponseHandler.handleSuccess(response)
            } catch (e: Exception) {
                Log.e("Network Error", "${e.printStackTrace()}")
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

            _movieAddedToDatabase.value = newMovie

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
            try {
                ImdbApi.cancelRequests() // Cancel previous requests

                val response: MovieResponse = retrofitService.getMovieFromId(searchResult.id)
                val newMovie = response.toDataBaseModel() // Convert network object to database model
                _movieDetailsObject.value = newMovie // Change to this notifies a navigation change

            } catch (e: UnknownHostException) {
                _toastMessageEvent.postValue(R.string.network_no_internet_message)
                Log.e("Network Error", "${e.printStackTrace()}")
            } catch (e: IOException) {
                _toastMessageEvent.postValue(R.string.network_error_message)
                Log.e("Network Error", "${e.printStackTrace()}")
            }
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

    override fun onCleared() {
        super.onCleared()

        // Cancel existing network requests when view model is destroyed
        try {
            ImdbApi.cancelRequests()
        } catch (e: IOException) {
            Log.e("Request Cancelled", "${e.printStackTrace()}")
        }
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