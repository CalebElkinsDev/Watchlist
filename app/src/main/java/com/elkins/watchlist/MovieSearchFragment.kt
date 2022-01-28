package com.elkins.watchlist

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.elkins.watchlist.database.MovieDatabase
import com.elkins.watchlist.database.MovieSearchViewModel
import com.elkins.watchlist.database.MovieSearchViewModelFactory
import com.elkins.watchlist.databinding.FragmentMovieSearchBinding
import com.elkins.watchlist.network.SearchResponse
import com.elkins.watchlist.utility.Resource
import com.elkins.watchlist.utility.Status


class MovieSearchFragment() : Fragment() {

    private lateinit var binding: FragmentMovieSearchBinding
    private lateinit var viewModel: MovieSearchViewModel
    private lateinit var searchAdapter: SearchResultListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_search, container, false)

        // Get a reference to the database and setup the viewmodel with the dao
        val database = MovieDatabase.getInstance(requireContext())
        val repository = MovieRepository(database.movieDao)
        val viewModelFactory = MovieSearchViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MovieSearchViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner

        // Create and assign a new adapter for the search results list
        searchAdapter = SearchResultListAdapter(AddClickListener {
            Log.d("Click", "Search Result: $it")
            viewModel.addMovieToRepository(it)
        })

        binding.searchResultsRecycler.adapter = searchAdapter

        // Observe the Resource ive data of the view model
        viewModel.results.observe(viewLifecycleOwner, { resource ->
            handleResourceSearchResponses(resource)
        })

        // Begin searching and hide the keyboard if it is visible
        binding.searchSearchButton.setOnClickListener {
            hideKeyboard(requireActivity())
            getQueryAndSearch()
        }

        // begin searching if the enter key is pressed on the virtual keyboard
        binding.searchSearchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getQueryAndSearch()
            }
            false
        }

        return binding.root
    }

    // Helper function for handling the different response types(Success, Error) of the search live data
    private fun handleResourceSearchResponses(resource: Resource<SearchResponse>) {
        when(resource.status) {
            Status.SUCCESS ->  {
                val results = resource.data?.results
                if(!results.isNullOrEmpty()) {
                    // Submit the results to the recycler view
                    binding.loadingBar.visibility = View.INVISIBLE
                    searchAdapter.submitList(resource.data.results)
                } else {
                    // If no results were found, display message to user
                    binding.loadingBar.visibility = View.INVISIBLE
                    Toast.makeText(context, R.string.network_no_results, Toast.LENGTH_LONG).show()
                }
            }
            Status.ERROR -> {
                binding.loadingBar.visibility = View.INVISIBLE
                // Show error message
                Log.d("Error", "Observe Status ERROR. ${resource.message}")
            }
            Status.LOADING -> {
                Log.d("Loading", "Should not see. Case not utilized")
            }
        }
    }

    /* Called by search button and edit text keyboard. Gets the search field text and begins
     * the network request. Clears the existing list and displays a progress bar while waiting
     */
    private fun getQueryAndSearch() {
        val searchString = binding.searchSearchEditText.text.toString()
        if(searchString.isNotBlank()) {
            searchAdapter.submitList(emptyList())
            viewModel.searchForMovie(searchString)
            binding.loadingBar.visibility = View.VISIBLE
        }
    }

    // Helper method to hide virtual keyboard when search is started
    private fun hideKeyboard(activity: Activity) {
        val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}