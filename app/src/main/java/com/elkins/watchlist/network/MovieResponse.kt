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
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "fullTitle") val fullTitle: String,
    @Json(name = "image") val imageUrl: String,
    @Json(name = "releaseDate") val releaseDate: String,
    @Json(name = "runtimeStr") val runtime: String,
    @Json(name = "plot") val plot: String,
    @Json(name = "directors") val directors: String,
    @Json(name = "stars") val stars: String,
    @Json(name = "genres") val genres: String,
    @Json(name = "contentRating") val contentRating: String,
    @Json(name = "imDbRating") val imdbRating: String,
    @Json(name = "metacriticRating") val metacriticRating: String

    ) {

    private fun parseDate(inDate: String): Date? {
        try {
            return inputFormat.parse(inDate)
        } catch (e: ParseException) {
            Log.e("Parse", e.message.toString())
        }
        return null
    }

    fun toDataBaseModel(): Movie {
        return Movie(
            id = id,
            title = title,
            fullTitle = fullTitle,
            imageUrl = imageUrl,
            releaseDate = parseDate(releaseDate),
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
