package com.elkins.watchlist.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class SearchResult(
    val id: String,
    @Json(name = "image") val imageUrl: String?,
    val title: String?,
    val plot: String?
)