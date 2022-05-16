package com.elkins.watchlist.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*


/**
 * Database and domain model class for a movie. Contains most fields from the network request
 * [com.elkins.watchlist.network.MovieResponse] objects, but with additional user fields.
 *
 * @param id: Unique id given by IMDB for finding details about the movie.
 * @param dateAdded: Initialized to the current date when added to the database
 * @param haveSeen: Updated by user. Determines if movie is in the "watchlist" or "seen" list
 * @param userScore: 0-5 star rating given by the user.
 */
@Parcelize
@Entity(tableName = "movies_table")
data class Movie(
    @PrimaryKey val id: String,
    val title: String?,
    val fullTitle: String?,
    val imageUrl: String?,
    val releaseDate: Date?,
    val runtime: String? = "N/A",
    val plot: String? = "",
    val directors: String? = "N/A",
    val stars: String? = "N/A",
    val genres: String? = "N/A",
    val contentRating: String? = "N/A",
    val imdbRating: String? = "N/A",
    val metacriticRating: String? = "N/A",

    var dateAdded: Date? = null,
    var haveSeen: Boolean = false,
    var following: Boolean = false,
    var userScore: Float = 0.0f
) : Parcelable
