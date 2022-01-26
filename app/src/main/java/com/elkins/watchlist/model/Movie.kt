package com.elkins.watchlist.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "movies_table")
data class Movie(
    @PrimaryKey val id: String,
    val title: String,
    val fullTitle: String,
    val imageUrl: String,
    val releaseDate: Date?,
    val runtime: String,
    val plot: String,
    val directors: String,
    val stars: String,
    val genres: String,
    val contentRating: String = "N/A",
    val imdbRating: String = "N/A", // TODO Convert ratings to numeric types with custom moshi adapters
    val metacriticRating: String = "N/A",

    var haveSeen: Boolean = false,
    var following: Boolean = false,
    var userScore: Int = 0
)
