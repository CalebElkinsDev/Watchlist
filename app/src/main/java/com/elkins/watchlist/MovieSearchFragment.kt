package com.elkins.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.elkins.watchlist.database.MovieDatabase
import com.elkins.watchlist.database.MovieSearchViewModel
import com.elkins.watchlist.database.MovieSearchViewModelFactory
import com.elkins.watchlist.databinding.FragmentMovieSearchBinding

class MovieSearchFragment() : Fragment() {

    private lateinit var binding: FragmentMovieSearchBinding
    private lateinit var viewModel: MovieSearchViewModel

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

        binding.lifecycleOwner = this

        // Create and assign a new adapter for the search results list
        val adapter = SearchResultListAdapter()
        binding.searchResultsRecycler.adapter = adapter

        // Update the recycler view once the search response returns
        viewModel.results.observe(viewLifecycleOwner, { searchResponse ->
            if(searchResponse.results.isNotEmpty()) {
                adapter.submitList(searchResponse.results)
            }
        })

        // Testing for search bar
        binding.searchSearchButton.setOnClickListener {
            val searchString = binding.searchSearchEditText.text.toString()
            viewModel.searchForMovie(searchString)
        }

        return binding.root
    }
}