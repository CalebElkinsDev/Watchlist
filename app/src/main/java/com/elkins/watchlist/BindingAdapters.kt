package com.elkins.watchlist

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.*


// Use Glide to load a movie's poster as a thumbnail for the movie list
@BindingAdapter("thumbnail")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
            .load(uri)
            .apply(RequestOptions()
                .placeholder(R.drawable.ic_launcher_foreground) // TODO Create placeholder image
                .error(R.color.black))  // TODO Create error image
            .thumbnail(0.1f)
            .into(view)
    }
}

private val outputFormat = SimpleDateFormat("yyyy, MMMM d")
@BindingAdapter("releaseDate")
fun formatDate(view: TextView, date: Date?) {
    var dateString: String
    try {
        dateString = outputFormat.format(date)
    } catch (e: Exception) {
        dateString = "N/A"
        Log.e("Parse Error", "Could not parse date: ${e.message}")
    }
    return view.setText(dateString)
}