package com.example.moviefavorites.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moviefavorites.data.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi

@Dao
interface MovieDao {
    /**
     * Inserts a movie into the database.
     *
     * @param movie The movie entity to insert.
     * @return The row ID of the inserted movie.
     */
    @OptIn(InternalSerializationApi::class)
    @Insert
    suspend fun insert(movie: Movie): Long

    /**
     * Inserts multiple movies into the database.
     *
     * @param movies List of movie entities to insert.
     * @return List of row IDs of the inserted movies.
     */
    @OptIn(InternalSerializationApi::class)
    @Insert
    suspend fun insertAll(movies: List<Movie>): List<Long>

    /**
     * Updates an existing movie in the database.
     *
     * @param movie The movie entity with updated values.
     */
    @OptIn(InternalSerializationApi::class)
    @Update
    suspend fun update(movie: Movie)

    /**
     * Deletes a movie from the database.
     *
     * @param movie The movie entity to delete.
     */
    @OptIn(InternalSerializationApi::class)
    @Delete
    suspend fun delete(movie: Movie)

    /**
     * Retrieves all movies from the database as a Flow.
     *
     * @return Flow emitting the list of all movies.
     */
    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun getAllMovies(): Flow<List<Movie>>

    /**
     * Retrieves all favorite movies from the database as a Flow.
     *
     * @return Flow emitting the list of favorite movies.
     */
    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM movies WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteMovies(): Flow<List<Movie>>

    /**
     * Retrieves a specific movie by its ID.
     *
     * @param id The ID of the movie to retrieve.
     * @return Flow emitting the movie with the given ID, or null if not found.
     */
    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieById(id: Int): Flow<Movie?>

    /**
     * Searches for movies by title (case-insensitive).
     *
     * @param query The search query string.
     * @return Flow emitting the list of movies matching the query.
     */
    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchMoviesByTitle(query: String): Flow<List<Movie>>

    /**
     * Counts the total number of movies in the database.
     *
     * @return Flow emitting the count of movies.
     */
    @Query("SELECT COUNT(*) FROM movies")
    fun getMovieCount(): Flow<Int>
}