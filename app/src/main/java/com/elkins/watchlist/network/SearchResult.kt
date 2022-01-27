package com.elkins.watchlist.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    val id: String,
    val resultType: String,
    @Json(name = "image") val imageUrl: String,
    val title: String,
    val description: String
)