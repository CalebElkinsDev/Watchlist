package com.elkins.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elkins.watchlist.databinding.MovieListItemBinding
import com.elkins.watchlist.databinding.MovieListItemPosterBinding
import com.elkins.watchlist.databinding.MovieListItemSimpleBinding
import com.elkins.watchlist.model.Movie

class MovieListAdapter(private val updateScoreListener: UpdateMovieClickListener,
                       private val updateFollowingListener: UpdateMovieClickListener,
                       private val movieDetailsListener: UpdateMovieClickListener)
    : ListAdapter<Movie, MovieListAdapter.MovieListViewHolder>(MovieDiffCallback()) {

    enum class MovieLayout {
        FULL, SIMPLE, POSTER
    }

    private var currentMovieLayout = MovieLayout.FULL

    fun setMovieLayoutType(layout: MovieLayout) {
        currentMovieLayout = layout
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        return MovieListViewHolder.from(parent, currentMovieLayout)
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        holder.bind(getItem(position), updateScoreListener, updateFollowingListener)
        holder.itemView.setOnClickListener {
            movieDetailsListener.onClick(currentList[position])
        }
    }

    class MovieListViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Movie, updateScoreListener: UpdateMovieClickListener,
                      updateFollowingListener: UpdateMovieClickListener) {

            // Handle binding based on the the type of layout that was inflated
            when(binding) {
                is MovieListItemBinding -> binding.apply {
                    binding.movie = item

                    // Update the user's score for the movie
                    binding.itemRatingBar.setOnRatingBarChangeListener { _, score, _ ->
                        item.userScore = score.toInt()
                        updateScoreListener.onClick(item)
                    }

                    binding.itemSeenCheckBox.setOnCheckedChangeListener { _, checked ->
                        item.haveSeen = checked
                        updateFollowingListener.onClick(item)
                    }
                    binding.executePendingBindings()
                }

                is MovieListItemSimpleBinding -> binding.apply {
                    binding.movie = item

                    binding.itemSeenCheckBox.setOnCheckedChangeListener { _, checked ->
                        item.haveSeen = checked
                        updateFollowingListener.onClick(item)
                    }
                    binding.executePendingBindings()
                }

                is MovieListItemPosterBinding -> binding.apply {
                    binding.movie = item

                    // Update the user's score for the movie
                    binding.itemRatingBar.setOnRatingBarChangeListener { _, score, _ ->
                        item.userScore = score.toInt()
                        updateScoreListener.onClick(item)
                    }

                    binding.itemSeenCheckBox.setOnCheckedChangeListener { _, checked ->
                        item.haveSeen = checked
                        updateFollowingListener.onClick(item)
                    }
                    binding.executePendingBindings()
                }
            }

        }

        companion object {

            fun from(parent: ViewGroup, movieLayout: MovieLayout) : MovieListViewHolder {

                val inflater = LayoutInflater.from(parent.context)
                // Determine what type of layout to inflate based on the movieLayout parameter
                val binding = when(movieLayout) {
                    MovieLayout.POSTER -> MovieListItemPosterBinding.inflate(inflater, parent, false)
                    MovieLayout.SIMPLE -> MovieListItemSimpleBinding.inflate(inflater, parent, false)
                    else -> MovieListItemBinding.inflate(inflater, parent, false)
                }
                return MovieListViewHolder(binding)
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            // Compare the Imdb Id used as the primary key
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

class UpdateMovieClickListener(val clickListener: (movie: Movie) -> Unit) {
    fun onClick(movie: Movie) = clickListener(movie)
}