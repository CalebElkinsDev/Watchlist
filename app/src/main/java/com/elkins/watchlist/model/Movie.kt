package com.elkins.watchlist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies_table")
data class Movie(
    @PrimaryKey val id: String,
    val title: String,
    val fullTitle: String,
    val imageUrl: String,
    val releaseDate: String,
    val runtime: String,
    val plot: String,
    val directors: String,
    val stars: String,
    val genres: String,
    val contentRating: String,
    val imdbRating: Double,
    val metacriticRating: Int,

    var haveSeen: Boolean = false,
    var following: Boolean = false,
    var userScore: Int = 0
)
