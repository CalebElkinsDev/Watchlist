package com.elkins.watchlist.utility

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.elkins.watchlist.MovieListAdapter
import com.elkins.watchlist.MovieRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/*
 * ItemTouchHelper callback for deleting swiped items in the movie list
 *
 * TODO: Add undo option/snackbar
 */
class SwipeMovieCallback(private val adapter: MovieListAdapter,
                         private val repository: MovieRepository)
    : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        // Get the movie at the swiped view holder's position
        val movie = adapter.currentList[viewHolder.absoluteAdapterPosition]

        // Call the repository's delete method supplying the id of the swiped movie
        GlobalScope.launch {
            repository.deleteMovie(movie.id)
        }
    }

    // Not utilized
    override fun onMove(recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}