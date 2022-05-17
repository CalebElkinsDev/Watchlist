package com.elkins.watchlist.search_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elkins.watchlist.databinding.SearchListItemBinding
import com.elkins.watchlist.network.SearchResult

/**
 * A [ListAdapter] implementation that contains user search result items from the IMDB api.
 *
 * @param addClickListener: Click listener used for adding the movie to the local database
 * @param detailsClickListener: Click listener for navigating to the movie's details page.
 */
class SearchResultListAdapter(private val addClickListener: AddClickListener,
                              private val detailsClickListener: DetailsClickListener)
    : ListAdapter<SearchResult, SearchResultListAdapter.SearchResultViewHolder>(MovieSearchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position), addClickListener)
        holder.itemView.setOnClickListener {
            detailsClickListener.onClick(currentList[position])
        }
    }

    class SearchResultViewHolder(private val binding: SearchListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchResult, clickListener: AddClickListener) {
            binding.movie = item
            binding.searchItemAddToDatabaseButton.setOnClickListener {
                clickListener.onClick(item, absoluteAdapterPosition)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): SearchResultViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = SearchListItemBinding.inflate(inflater, parent, false)
                return SearchResultViewHolder(binding)
            }
        }
    }

    class MovieSearchDiffCallback : DiffUtil.ItemCallback<SearchResult>() {
        override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * Simple click listener interface for returning the [SearchResult] object and its current position.
 * the position is needed for removing from the list gracefully if database trasaciton is succesful.
 */
class AddClickListener(val clickListener: (searchResult: SearchResult, position: Int) -> Unit) {
    fun onClick(searchResult: SearchResult, position: Int) = clickListener(searchResult, position)
}

/** Simple click listener interface for retrieving the [SearchResult] object bound to a ViewHolder */
class DetailsClickListener(val clickListener: (searchResult: SearchResult) -> Unit) {
    fun onClick(searchResult: SearchResult) = clickListener(searchResult)
}