package com.elkins.watchlist.utility

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/** [ItemTouchHelper] callback for deleting swiped items in the movie list */
abstract class SwipeMovieCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.65f
    }
}