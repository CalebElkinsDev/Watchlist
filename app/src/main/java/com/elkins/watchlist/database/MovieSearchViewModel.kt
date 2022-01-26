package com.elkins.watchlist.database

import android.util.Log
import androidx.lifecycle.*
import com.elkins.watchlist.MovieRepository
import com.elkins.watchlist.network.ImdbApi
import com.elkins.watchlist.network.SearchResponse
import kotlinx.coroutines.launch

class MovieSearchViewModel(private val repository: MovieRepository) : ViewModel() {

    // Live data list of movies found from search
    private var _results = MutableLiveData<SearchResponse>()
    val results: LiveData<SearchResponse>
        get() = _results


    fun searchForMovie(searchString: String) {
        viewModelScope.launch {
            // Get movies saved to local database
            val response = ImdbApi.retrofitService.searchForMovie(searchString) // TODO use searchString
            _results.value = response

            Log.d("Network", "Response: Search=${response.expression}, results=${response.results.size}")
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