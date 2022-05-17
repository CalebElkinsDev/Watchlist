package com.elkins.watchlist.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Network response object that contains broad details about a movie and its unique ID.
 *
 * @param id: The unique ID for the movie used across IMDB api. Used when fetching details.
 */
@JsonClass(generateAdapter = false)
data class SearchResult(
    val id: String,
    @Json(name = "image") val imageUrl: String?,
    val title: String?,
    val description: String?, // Contains release year as (yyyy), e.g., "(2001)"
    val runtimeStr: String?,
    val genres: String?,
    val contentRating: String?,
    val plot: String?
)