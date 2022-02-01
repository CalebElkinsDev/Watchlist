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
import androidx.navigation.fragment.findNavController
import com.elkins.watchlist.database.MovieDatabase
import com.elkins.watchlist.database.MovieSearchViewModel
import com.elkins.watchlist.database.MovieSearchViewModelFactory
import com.elkins.watchlist.databinding.FragmentMovieSearchBinding
import com.elkins.watchlist.model.Movie
import com.elkins.watchlist.network.SearchResponse
import com.elkins.watchlist.utility.Resource
import com.elkins.watchlist.utility.Status
import com.elkins.watchlist.utility.setSupportBarTitle


class MovieSearchFragment() : Fragment() {

    private lateinit var binding: FragmentMovieSearchBinding
    private lateinit var viewModel: MovieSearchViewModel
    private lateinit var searchAdapter: SearchResultListAdapter
    private lateinit var repository: MovieRepository

    // Update app bar title on resume to override changes in other fragments
    override fun onResume() {
        super.onResume()
        setSupportBarTitle(requireActivity(), getString(R.string.search_fragment_title))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_search, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initializeViewModel()
        initializeRecyclerView()
        initializeLiveDataObservers()

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

    private fun initializeViewModel() {
        // Get a reference to the database and setup the viewmodel with the dao
        val database = MovieDatabase.getInstance(requireContext())
        repository = MovieRepository(database.movieDao)
        val viewModelFactory = MovieSearchViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MovieSearchViewModel::class.java)
    }

    private fun initializeRecyclerView() {
        // Create and assign a new adapter for the search results list
        searchAdapter = SearchResultListAdapter(AddClickListener { searchResult, position ->
            // Get movie details and add the Movie to the watchlist
            viewModel.addMovieToRepository(searchResult, position)
        }, DetailsClickListener {
            viewModel.getMovieDetailsAndBeginNavigation(it)
            binding.loadingBar.visibility = View.VISIBLE // Display progressbar
        })

        binding.searchResultsRecycler.adapter = searchAdapter
    }

    private fun initializeLiveDataObservers() {
        // Observe the Resource ive data of the view model
        viewModel.results.observe(viewLifecycleOwner, { resource ->
            handleResourceSearchResponses(resource)
        })

        // Observe the LiveData Movie object that is created when navigation is requested
        viewModel.movieDetailsObject.observe(viewLifecycleOwner, {
            it?.let {
                binding.loadingBar.visibility = View.INVISIBLE // Disable loading bar
                navigateToMovieDetails(it)
                viewModel.navigationToDetailsComplete() // Let view model know navigation is handled
            }
        })

        // Observer for notifying adapter when an item has been removed
        viewModel.lastRemovedIndex.observe(viewLifecycleOwner, {
            searchAdapter.notifyItemRemoved(it)
        })

        // Display the toast message posted in the view model's LiveData toast event variable
        viewModel.toastMessageEvent.observe(viewLifecycleOwner, { resourceId ->
            resourceId?.let {
                Toast.makeText(requireActivity(), getString(it), Toast.LENGTH_LONG).show()
                viewModel.toastMessageEventComplete() // Notify view model that event was handled
            }
        })
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
                    Toast.makeText(context, R.string.search_no_results, Toast.LENGTH_LONG).show()
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

    private fun navigateToMovieDetails(movie: Movie) {
        findNavController().navigate(MovieSearchFragmentDirections
            .actionMovieSearchFragmentToMovieDetailsFragment(movie, false))
    }
}