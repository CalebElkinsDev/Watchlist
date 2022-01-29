package com.elkins.watchlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.elkins.watchlist.MovieRepository.MovieFilter
import com.elkins.watchlist.database.MovieDatabase
import com.elkins.watchlist.databinding.FragmentMovieListBinding
import com.elkins.watchlist.model.Movie
import com.elkins.watchlist.utility.SwipeMovieCallback


class MovieListFragment : Fragment() {

    private lateinit var binding: FragmentMovieListBinding
    private lateinit var viewModel: MovieListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_list, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        // Get a reference to the database and setup the viewmodel with the dao
        val database = MovieDatabase.getInstance(requireContext())
        val repository = MovieRepository(database.movieDao)
        val viewModelFactory = MovieListViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(MovieListViewModel::class.java)

        // Create and assign a new adapter for the saved movie list
        val adapter = MovieListAdapter(UpdateMovieClickListener { viewModel.updateMovieScore(it.userScore, it.id) },
            UpdateMovieClickListener { viewModel.updateHaveSeenMovie(it.haveSeen, it.id) },
            UpdateMovieClickListener { openMovieDetails(it) })

        // Assign the list adapter to the recycler view
        binding.movieListRecycler.adapter = adapter

        // Create a callback for deleting movies when swiped
        val swipeCallback = ItemTouchHelper(SwipeMovieCallback(adapter, repository))
        swipeCallback.attachToRecyclerView(binding.movieListRecycler)

        // Observe the viewmodel's movie list and submit it to the adapter when changed
        viewModel.movies.observe(viewLifecycleOwner, { movies ->
            if(movies != null) {
                adapter.submitList(movies)
                Log.d("Filter", "observe data change")
            }
        })

        // Navigate to Movie Search fragment when FAB is clicked
        binding.listAddNewMovieButton.setOnClickListener {
            openNewMovieSearchFragment()
        }

        /* Setup spinners for filtering and sorting the list */
        initializeFilterSpinner()
        initializeSortSpinner()

        return binding.root
    }

    private fun initializeFilterSpinner() {
        val filterAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.filter_types,
            android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.listFilterSpinner.adapter = filterAdapter
        binding.listFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?,
                                        position: Int, id: Long) {
                val filter = when(position) {
                    0 -> MovieFilter.ALL
                    1 -> MovieFilter.UNSEEN
                    else -> MovieFilter.SEEN
                }
                viewModel.updateFilter(filter)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun initializeSortSpinner() {
        val sortAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.sort_type,
            android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.listTypeSpinner.adapter = sortAdapter
    }

    private fun openNewMovieSearchFragment() {
        findNavController().navigate(MovieListFragmentDirections
            .actionMovieListFragmentToMovieSearchFragment())
    }

    private fun openMovieDetails(movie: Movie) {
        findNavController().navigate(MovieListFragmentDirections
            .actionMovieListFragmentToMovieDetailsFragment(movie))
    }
}