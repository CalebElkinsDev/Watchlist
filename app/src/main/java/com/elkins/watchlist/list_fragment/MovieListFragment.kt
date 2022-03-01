package com.elkins.watchlist.list_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elkins.watchlist.R
import com.elkins.watchlist.database.MovieDatabase
import com.elkins.watchlist.database.MovieRepository
import com.elkins.watchlist.database.MovieRepository.SortType
import com.elkins.watchlist.databinding.FragmentMovieListBinding
import com.elkins.watchlist.model.Movie
import com.elkins.watchlist.utility.MovieLayoutType
import com.elkins.watchlist.utility.SwipeMovieCallback
import com.elkins.watchlist.utility.setSupportBarTitle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MovieListFragment : Fragment() {

    private lateinit var binding: FragmentMovieListBinding
    private lateinit var viewModel: MovieListViewModel
    private lateinit var movieListAdapter: MovieListAdapter
    private lateinit var repository: MovieRepository

    // Update app bar title on resume to override changes in other fragments
    override fun onResume() {
        super.onResume()
        setSupportBarTitle(requireActivity(), getString(R.string.app_name))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_list, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initializeViewModel()
        initializeRecyclerView()
        setupMovieSwipeCallback()
        initializeLiveDataObservers()

        /* Setup spinners for filtering and sorting the list */
        initializeFilterSwitch()
        initializeSortSpinner()
        initializeSortOrderButton()

        // Set the view model's listType live data to the next enum value and trigger a observer change
        binding.listLayoutChangeButton.setOnClickListener {
            viewModel.cycleListType()
        }

        // Navigate to Movie Search fragment when FAB is clicked
        binding.listAddNewMovieButton.setOnClickListener {
            openNewMovieSearchFragment()
        }

        return binding.root
    }

    private fun initializeViewModel() {
        // Get a reference to the database and setup the view model with the dao
        val database = MovieDatabase.getInstance(requireContext())
        repository = MovieRepository(database.movieDao)
        val viewModelFactory = MovieListViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(MovieListViewModel::class.java)
    }

    private fun initializeRecyclerView() {
        // Create and assign a new adapter for the saved movie list
        movieListAdapter = MovieListAdapter(
            UpdateMovieClickListener {
                viewModel.updateMovieScore(it.userScore, it.id)
            },
            UpdateMovieClickListener {
                viewModel.updateHaveSeenMovie(it.haveSeen, it.id)
            },
            UpdateMovieClickListener {
                navigateToMovieDetails(it)
            })

        // Assign the list adapter to the recycler view
        binding.movieListRecycler.adapter = movieListAdapter
    }

    private fun setupMovieSwipeCallback() {
        // Create a callback for deleting movies when swiped
        val swipeCallback = ItemTouchHelper(object : SwipeMovieCallback() {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Get the movie at the swiped view holder's position
                val movie = movieListAdapter.currentList[viewHolder.absoluteAdapterPosition]

                // Call the repository's delete method supplying the id of the swiped movie
                GlobalScope.launch {
                    repository.deleteMovie(movie.id)

                    val snackBar = Snackbar.make(binding.root,
                        R.string.list_movie_removed_snackbar_message, Snackbar.LENGTH_LONG)

                    snackBar.apply {
                        setAction(R.string.list_movie_removed_snackbar_action) {
                            // Add the movie back to the database if the user presses the action button
                            GlobalScope.launch {
                                repository.addMovie(movie)
                            }
                        }
                        setTextColor(resources.getColor(R.color.secondaryTextColor))
                        setActionTextColor(resources.getColor(R.color.primaryTextColor))
                    }
                    snackBar.show()
                }
            }

            // Not utilized
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Log.d("Item Swipe","Not yet implemented")
                return false
            }
        })
        swipeCallback.attachToRecyclerView(binding.movieListRecycler)
    }

    private fun initializeLiveDataObservers() {
        // Handle updates to listType live data by updating the UI and updating the recycler view
        // Handled with view model live data to survive configuration changes
        viewModel.currentListType.observe(viewLifecycleOwner, {
            binding.listLayoutChangeButton.background = when(it) {
                MovieLayoutType.SIMPLE -> ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_layout_simple
                )
                MovieLayoutType.POSTER -> ContextCompat.getDrawable(requireActivity(),
                    R.drawable.ic_layout_poster
                )
                else -> ContextCompat.getDrawable(requireActivity(), R.drawable.ic_layout_normal)
            }
            setListLayout(it)
        })

        // Observe the total seen movies count
        viewModel.watchedMoviesCount.observe(viewLifecycleOwner, {
            it?.let {
                binding.listSeenMovieTotalTextView.text = it.toString()
            }
        })

        // Observe the total non sen movies count
        viewModel.notWatchedMoviesCount.observe(viewLifecycleOwner, {
            it?.let {
                binding.listUnseenMoviesTextView.text = it.toString()
            }
        })

        setObserverForCurrentList()
    }

    /* Function to (re)initialize observation of the current movie list LiveData of the view model*/
    private fun setObserverForCurrentList() {
        viewModel.movies.observe(viewLifecycleOwner, { movies ->
            Log.d("Database", "View model observer fired")
            movieListAdapter.submitList(movies)
        })
    }

    private fun initializeFilterSwitch() {
        binding.listFilterSwitch.setOnCheckedChangeListener { _, on ->
            viewModel.setShowWatched(on)
        }
    }

    private fun initializeSortSpinner() {
        val sortAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.sort_types,
            android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.listTypeSpinner.adapter = sortAdapter
        binding.listTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?,
                                        position: Int, id: Long) {
                val sortType = when(position) {
                    0 -> SortType.TITLE
                    1 -> SortType.RELEASE_DATE
                    else -> SortType.DATE_ADDED
                }
                viewModel.setSortType(sortType)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun initializeSortOrderButton() {
        binding.listSortOrderToggleButton.setOnCheckedChangeListener { _, ascending ->
            // on == ascending
            viewModel.setSortAscending(ascending)
        }
    }

    private fun setListLayout(layoutType: MovieLayoutType) {

        when(layoutType) {
            MovieLayoutType.FULL -> {
                binding.movieListRecycler.layoutManager = LinearLayoutManager(requireActivity())
                movieListAdapter.setMovieLayoutType(MovieLayoutType.FULL)
                binding.movieListRecycler.adapter = movieListAdapter
            }
            MovieLayoutType.SIMPLE -> {
                binding.movieListRecycler.layoutManager = LinearLayoutManager(requireActivity())
                movieListAdapter.setMovieLayoutType(MovieLayoutType.SIMPLE)
                binding.movieListRecycler.adapter = movieListAdapter
            }
            MovieLayoutType.POSTER -> {
                binding.movieListRecycler.layoutManager = GridLayoutManager(requireActivity(), 2)
                movieListAdapter.setMovieLayoutType(MovieLayoutType.POSTER)
                binding.movieListRecycler.adapter = movieListAdapter
            }
        }
    }

    private fun openNewMovieSearchFragment() {
        findNavController().navigate(MovieListFragmentDirections
            .actionMovieListFragmentToMovieSearchFragment())
    }

    private fun navigateToMovieDetails(movie: Movie) {
        findNavController().navigate(MovieListFragmentDirections
            .actionMovieListFragmentToMovieDetailsFragment(movie, true))
    }
}