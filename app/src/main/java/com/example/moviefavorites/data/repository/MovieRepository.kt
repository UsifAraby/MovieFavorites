/**
 * Repository interface and implementation for movie data operations.
 */
package com.example.moviefavorites.data.repository

import com.example.moviefavorites.data.model.Movie
import kotlinx.coroutines.flow.Flow


interface MovieRepository {

    fun getAllMovies(): Flow<List<Movie>>


    fun getFavoriteMovies(): Flow<List<Movie>>



    fun searchMoviesByTitle(query: String): Flow<List<Movie>>


    fun getMovieById(id: Int): Flow<Movie?>


    suspend fun insertMovie(movie: Movie)

    suspend fun insertMovies(movies: List<Movie>)


    suspend fun updateMovie(movie: Movie)

    suspend fun deleteMovie(movie: Movie)

    suspend fun toggleFavorite(movie: Movie)
}


class MovieRepositoryImpl(
    private val movieDao: com.example.moviefavorites.data.room.MovieDao
) : MovieRepository {
    override fun getAllMovies(): Flow<List<Movie>> = movieDao.getAllMovies()

    override fun getFavoriteMovies(): Flow<List<Movie>> = movieDao.getFavoriteMovies()

    override fun searchMoviesByTitle(query: String): Flow<List<Movie>> = movieDao.searchMoviesByTitle(query)

    override fun getMovieById(id: Int): Flow<Movie?> = movieDao.getMovieById(id)

    override suspend fun insertMovie(movie: Movie) {
        movieDao.insert(movie)
    }

    override suspend fun insertMovies(movies: List<Movie>) {
        movieDao.insertAll(movies)
    }

    override suspend fun updateMovie(movie: Movie) {
        movieDao.update(movie)
    }

    override suspend fun deleteMovie(movie: Movie) {
        movieDao.delete(movie)
    }

    override suspend fun toggleFavorite(movie: Movie) {
        val updated = movie.copy(isFavorite = !movie.isFavorite)
        movieDao.update(updated)
    }
}
