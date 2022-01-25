package com.elkins.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.elkins.watchlist.database.MovieDatabase
import com.elkins.watchlist.databinding.FragmentMovieListBinding


class MovieListFragment : Fragment() {

    private lateinit var binding: FragmentMovieListBinding
    private lateinit var viewModel: MovieListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_list, container, false)

        // Get a reference to the database and setup the viewmodel with the dao
        val database = MovieDatabase.getInstance(requireContext())
        val repository = MovieRepository(database.movieDao)
        val viewModelFactory = MovieListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MovieListViewModel::class.java)

        binding.lifecycleOwner = this

        // Create and assign a new adapter for the saved movie list
        val adapter = MovieListAdapter()
        binding.movieListRecycler.adapter = adapter

        // Observe the viewmodel's movie list and submit it to the adapter when changed
        viewModel.movies.observe(viewLifecycleOwner, { movies ->
            if(movies != null) {
                adapter.submitList(movies)
            }
        })

        return binding.root
    }

}