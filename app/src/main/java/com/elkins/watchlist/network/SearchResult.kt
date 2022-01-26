package com.elkins.watchlist.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class SearchResult(
    @Json(name = "id") val id: String,
    @Json(name = "resultType") val resultType: String,
    @Json(name = "image") val imageUrl: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String
    )
