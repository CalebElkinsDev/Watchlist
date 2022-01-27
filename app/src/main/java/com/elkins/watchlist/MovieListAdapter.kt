package com.elkins.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elkins.watchlist.databinding.MovieListItemBinding
import com.elkins.watchlist.model.Movie

class MovieListAdapter(private val updateScoreListener: UpdateMovieClickListener,
                       private val updateFollowingListener: UpdateMovieClickListener)
    : ListAdapter<Movie, MovieListAdapter.MovieListViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        return MovieListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        holder.bind(getItem(position), updateScoreListener, updateFollowingListener)
    }

    class MovieListViewHolder(private val binding: MovieListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Movie, updateScoreListener: UpdateMovieClickListener,
                 updateFollowingListener: UpdateMovieClickListener) {
            binding.movie = item

            // Update the user's score for the movie
            binding.itemRatingBar.setOnRatingBarChangeListener { _, score, _ ->
                item.userScore = score.toInt()
                updateScoreListener.onClick(item)
            }

            binding.itemSeenCheckBox.setOnCheckedChangeListener { _, checked ->
                item.following = checked
                updateFollowingListener.onClick(item)
            }

            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): MovieListViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val binding = MovieListItemBinding.inflate(inflater, parent, false)

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