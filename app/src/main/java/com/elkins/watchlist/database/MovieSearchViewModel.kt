package com.elkins.watchlist.database

import android.util.Log
import androidx.lifecycle.*
import com.elkins.watchlist.MovieRepository
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


    fun searchForMovie(searchString: String) {
        viewModelScope.launch {
            try {
                val response: SearchResponse = retrofitService.searchForMovie(searchString)
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

    fun addMovieToRepository(searchResult: SearchResult) {
        viewModelScope.launch {
            val response: MovieResponse = retrofitService.getMovieFromId(searchResult.id)
            Log.d("Network", "Response: = $response")
            val newMovie = response.toDataBaseModel() // Convert network object to database model
            newMovie.dateAdded = Calendar.getInstance().time // Set the dateAdded to be the current time
            repository.addMovie(newMovie) // Add the new movie to the repo after setting the time

            // Remove the added movie from the results and resubmit
            val editedSearchResponse = _results.value?.data
            editedSearchResponse?.results!!.remove(searchResult)
            _results.value = Resource.success(editedSearchResponse)
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