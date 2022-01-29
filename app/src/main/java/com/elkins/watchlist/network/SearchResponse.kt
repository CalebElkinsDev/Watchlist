package com.elkins.watchlist.network

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SearchResponse(val searchType: String,
                          val expression: String,
                          var results: MutableList<SearchResult>
)