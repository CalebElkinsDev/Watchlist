package com.elkins.watchlist.network

import android.util.Log
import com.elkins.watchlist.model.Movie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


private val inputFormat by lazy {
    SimpleDateFormat("yyyy-MM-dd")
}

@JsonClass(generateAdapter = false)
data class MovieResponse (
    val id: String,
    val title: String? = "N/A",
    val fullTitle: String? = "N/A",
    @Json(name = "image") val imageUrl: String?,
    val releaseDate: String? = "TBD",
    @Json(name = "runtimeStr") val runtime: String?,
    val plot: String?,
    val directors: String?,
    val stars: String?,
    val genres: String?,
    val contentRating: String? = "NR",
    @Json(name = "imDbRating") val imdbRating: String?,
    val metacriticRating: String?
    ) {

    private fun parseDate(inDate: String): Date? {
        try {
            return inputFormat.parse(inDate)
        } catch (e: ParseException) {
            Log.e("Parse", e.message.toString())
            return null
        }
    }

    fun toDataBaseModel(): Movie {
        var parsedDate: Date? = null
        if(!releaseDate.isNullOrBlank()) {
            parsedDate = parseDate(releaseDate)
        }

        return Movie(
            id = id,
            title = title,
            fullTitle = fullTitle,
            imageUrl = imageUrl,
            releaseDate = parsedDate,
            runtime = runtime,
            plot = plot,
            directors = directors,
            stars = stars,
            genres = genres,
            contentRating = contentRating,
            imdbRating = imdbRating,
            metacriticRating = metacriticRating
        )
    }
}
