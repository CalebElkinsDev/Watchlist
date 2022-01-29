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


class MovieDetailsFragment : Fragment() {

    private lateinit var binding: FragmentMovieDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false)

        if(arguments != null) {
            val movie: Movie = MovieDetailsFragmentArgs.fromBundle(requireArguments()).movie
            binding.movie = movie
        } else {
            // TODO: Handle null movie
            Log.e("Navigation Error", "ERROR ERROR ERROR")
        }

        return binding.root
    }
}