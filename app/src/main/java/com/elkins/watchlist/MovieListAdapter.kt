package com.elkins.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elkins.watchlist.databinding.MovieListItemBinding
import com.elkins.watchlist.model.Movie

class MovieListAdapter : ListAdapter<Movie, MovieListAdapter.MovieListViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        return MovieListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieListViewHolder(private val binding: MovieListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Movie) {
            binding.movie = item
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
            return oldItem == newItem
        }
    }
}