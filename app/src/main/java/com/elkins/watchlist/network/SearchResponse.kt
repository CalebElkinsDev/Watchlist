package com.elkins.watchlist.network

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SearchResponse(val queryString: String,
                          var results: MutableList<SearchResult>,
                          val errorMessage: String?)