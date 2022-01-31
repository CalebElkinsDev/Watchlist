package com.elkins.watchlist.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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