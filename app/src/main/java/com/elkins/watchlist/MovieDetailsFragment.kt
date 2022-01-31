package com.elkins.watchlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.elkins.watchlist.databinding.FragmentMovieDetailsBinding
import com.elkins.watchlist.model.Movie
import com.elkins.watchlist.utility.setSupportBarTitle


class MovieDetailsFragment : Fragment() {

    private lateinit var binding: FragmentMovieDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false)

        if(arguments != null) {
            // Get the navigation arguments and pass them to the data binding layout
            val movie: Movie = MovieDetailsFragmentArgs.fromBundle(requireArguments()).movie
            binding.movie = movie

            val navigatedFromList: Boolean = MovieDetailsFragmentArgs.fromBundle(requireArguments()).fromListFragment
            binding.navigatedFromList = navigatedFromList

            // Set the title of the app bar as the title or a placeholder
            val title = movie.fullTitle ?: movie.title ?: getString(R.string.details_appbar_placeholder_title)
            setSupportBarTitle(requireActivity(), title)
        } else {
            // TODO: Handle null movie
            Log.e("Navigation Error", "ERROR ERROR ERROR")
        }

        return binding.root
    }
}