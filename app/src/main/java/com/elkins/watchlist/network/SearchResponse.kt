package com.elkins.watchlist.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = false)
data class SearchResponse(
    @Json(name = "searchType") val searchType: String,
    @Json(name = "expression") val expression: String,
    @Json(name = "results") val results: List<SearchResult>
)