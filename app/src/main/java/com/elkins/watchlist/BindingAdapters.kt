package com.elkins.watchlist

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
            .transform(CenterCrop(), RoundedCorners(16))
            .into(view)
    }
}

private val outputFormat = SimpleDateFormat("yyyy, MMMM d")
@BindingAdapter("releaseDate")
fun formatDate(view: TextView, date: Date) {
    return view.setText(outputFormat.format(date))
}