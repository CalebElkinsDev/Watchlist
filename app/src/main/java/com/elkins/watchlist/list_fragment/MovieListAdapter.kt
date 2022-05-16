package com.elkins.watchlist.list_fragment

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
import com.elkins.watchlist.utility.MovieLayoutType

/** ListAdapter implentation for the main movie list of the application.
 *
 * Layouts are constructed based on the user's current layout preference stored in a
 * [MovieLayoutType] variable and stored in preferences. Multiple click listeners are registered
 * when creating the adapater. One for clicking on the entire ViewHolder, and two more(depending
 * on layout type) for user variables(haveSeen, userScore).
 *
 * @param updateScoreListener: Click listener that updates the user score for the bound [Movie]
 * @param updateHaveSeenListener: Click listener that updates the "haveSeen" variable of the bound [Movie]
 * @param movieDetailsListener: Click Listener that starts navigation to the [com.elkins.watchlist.MovieDetailsFragment] with the bound [Movie]
 */
class MovieListAdapter(private val updateScoreListener: UpdateMovieClickListener,
                       private val updateHaveSeenListener: UpdateMovieClickListener,
                       private val movieDetailsListener: UpdateMovieClickListener)
    : ListAdapter<Movie, MovieListAdapter.MovieListViewHolder>(MovieDiffCallback()) {

    // Curent layout-type for the adapater
    private var currentMovieLayout = MovieLayoutType.FULL

    // Public function for changing the layout type
    fun setMovieLayoutType(layoutType: MovieLayoutType) {
        currentMovieLayout = layoutType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        return MovieListViewHolder.from(parent, currentMovieLayout)
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        /** Use the MovieListViewHolder.bind function to determine how to handle the click
         * listeners and assign the correct [Movie] object */
        holder.bind(getItem(position), updateScoreListener, updateHaveSeenListener)

        /** Set the click listener for the entire ViewHolder that navigates to the Details fragment.
         * "When" statement necessary to corporate between the various layout types. */
        holder.itemView.setOnClickListener {
            val movieRef: Movie? = when(currentMovieLayout) {
                MovieLayoutType.FULL -> (holder.binding as MovieListItemBinding).movie
                MovieLayoutType.SIMPLE -> (holder.binding as MovieListItemSimpleBinding).movie
                MovieLayoutType.POSTER -> (holder.binding as MovieListItemPosterBinding).movie
            }
            movieDetailsListener.onClick(movieRef!!)
        }
    }

    class MovieListViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Bind the current Movie and listeners to this holder.
         *
         * Due to the various data-binding layouts used for the adapter, a "when" case is used
         * to apply the necessary bindings based on current [MovieLayoutType]. Repeated code was
         * seemingly unavoidable as the "binding" variable could not be used generically.
         *
         * @param item: The current [Movie] from the list to bind to
         * @param updateHaveSeenListener: Listener passed from the parent [MovieListAdapter] for updating "haveSeen"
         * @param updateScoreListener: Listener passed from the parent [MovieListAdapter] for updating "userScore"
         */
        fun bind(item: Movie, updateScoreListener: UpdateMovieClickListener,
                 updateHaveSeenListener: UpdateMovieClickListener) {

            // Handle binding based on the the type of layout that was inflated
            when(binding) {
                is MovieListItemBinding -> binding.apply {
                    binding.movie = item

                    // Update the user's score for the movie
                    binding.itemRatingBar.setOnRatingBarChangeListener { _, score, _ ->
                        item.userScore = score
                        updateScoreListener.onClick(item)
                    }

                    binding.itemSeenCheckBox.setOnCheckedChangeListener { _, checked ->
                        item.haveSeen = checked
                        updateHaveSeenListener.onClick(item)
                    }
                    binding.executePendingBindings()
                }

                is MovieListItemSimpleBinding -> binding.apply {
                    binding.movie = item
                    binding.itemSeenCheckBox.setOnCheckedChangeListener { _, checked ->
                        item.haveSeen = checked
                        updateHaveSeenListener.onClick(item)
                    }
                    binding.executePendingBindings()
                }

                is MovieListItemPosterBinding -> binding.apply {
                    binding.movie = item
                    binding.itemSeenCheckBox.setOnCheckedChangeListener { _, checked ->
                        item.haveSeen = checked
                        updateHaveSeenListener.onClick(item)
                    }
                    binding.executePendingBindings()
                }
            }
        }

        companion object {
            /**
             * Companion function for instantiating a [MovieListViewHolder]
             *
             * @param movieLayoutType: A [MovieLayoutType] that determines what layout to inflate
             */
            fun from(parent: ViewGroup, movieLayoutType: MovieLayoutType) : MovieListViewHolder {
                val inflater = LayoutInflater.from(parent.context)

                /** Inflate the corresponding DatabindinLayout based on the [MovieLayoutType] */
                val binding = when(movieLayoutType) {
                    MovieLayoutType.POSTER -> MovieListItemPosterBinding.inflate(inflater, parent, false)
                    MovieLayoutType.SIMPLE -> MovieListItemSimpleBinding.inflate(inflater, parent, false)
                    else -> MovieListItemBinding.inflate(inflater, parent, false)
                }
                return MovieListViewHolder(binding)
            }
        }
    }

    /** Diffutil.ItemCallback for ListAdapter. Compares the [Movie] Id in both cases */
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

/** Simple callback class used for handling clicks on items in the list. */
class UpdateMovieClickListener(val clickListener: (movie: Movie) -> Unit) {
    fun onClick(movie: Movie) = clickListener(movie)
}