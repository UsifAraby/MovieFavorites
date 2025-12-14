package com.example.moviefavorites.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi //Marked with @Serializable for JSON serialization to pass to Flutter app.
@Serializable
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val year: Int,
    val genre: String,
    val rating: Double,
    val description: String,
    val url: String,
    val posterUrl: String = "https://via.placeholder.com/150",
    val isFavorite: Boolean = false
)