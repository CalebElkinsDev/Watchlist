package com.elkins.watchlist.network

import com.squareup.moshi.JsonClass


/**
 * Network response class for the IMDB search query
 *
 * @param queryString: The search input from the user(i.e., movie title)
 * @param results: A list of [SearchResult]s (if any) based on the queryString. Each [SearchResult]
 * contains a unique ID that can then used for a movie details call.
 */
@JsonClass(generateAdapter = true)
data class SearchResponse(val queryString: String,
                          var results: MutableList<SearchResult>,
                          val errorMessage: String?)