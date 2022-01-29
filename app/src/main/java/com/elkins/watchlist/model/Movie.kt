package com.elkins.watchlist.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

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
    val imdbRating: String? = "N/A", // TODO Convert ratings to numeric types with custom moshi adapters
    val metacriticRating: String? = "N/A",

    var dateAdded: Date? = null,
    var haveSeen: Boolean = false,
    var following: Boolean = false,
    var userScore: Int = 0
) : Parcelable
