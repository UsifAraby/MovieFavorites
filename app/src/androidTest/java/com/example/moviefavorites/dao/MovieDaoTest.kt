/**
 * Unit tests for MovieDao using in-memory Room database.
 */
package com.example.moviefavorites.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moviefavorites.data.model.Movie
import com.example.moviefavorites.data.room.MovieDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.InternalSerializationApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for MovieDao.
 * Uses an in-memory Room database for testing CRUD operations that are used in the app.
 */
@OptIn(InternalSerializationApi::class)
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MovieDaoTest {
    private lateinit var database: MovieDatabase
    private lateinit var movieDao: com.example.moviefavorites.data.room.MovieDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            MovieDatabase::class.java
        ).allowMainThreadQueries().build()

        movieDao = database.movieDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertMovie() = runTest(testDispatcher) {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Test description",
            url = "https://example.com"
        )

        movieDao.insert(movie)

        // Verify the movie was inserted
        val movies = movieDao.getAllMovies().first()
        assert(movies.size == 1)
        assert(movies[0].title == "Test Movie")
    }

    @Test
    fun testInsertMultipleMovies() = runTest(testDispatcher) {
        val movies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                year = 2024,
                genre = "Action",
                rating = 8.5,
                description = "Description 1",
                url = "https://example1.com"
            ),
            Movie(
                id = 2,
                title = "Movie 2",
                year = 2023,
                genre = "Drama",
                rating = 7.5,
                description = "Description 2",
                url = "https://example2.com"
            )
        )

        movieDao.insertAll(movies)

        val insertedMovies = movieDao.getAllMovies().first()
        assert(insertedMovies.size == 2)
        assert(insertedMovies[0].title == "Movie 1")
        assert(insertedMovies[1].title == "Movie 2")
    }

    @Test
    fun testUpdateMovie() = runTest(testDispatcher) {
        val movie = Movie(
            id = 1,
            title = "Original Title",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Test description",
            url = "https://example.com"
        )

        movieDao.insert(movie)

        val updatedMovie = movie.copy(title = "Updated Title", isFavorite = true)
        movieDao.update(updatedMovie)

        val retrievedMovie = movieDao.getMovieById(1).first()
        assert(retrievedMovie?.title == "Updated Title")
        assert(retrievedMovie?.isFavorite == true)
    }

    @Test
    fun testDeleteMovie() = runTest(testDispatcher) {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Test description",
            url = "https://example.com"
        )

        movieDao.insert(movie)
        movieDao.delete(movie)

        val movies = movieDao.getAllMovies().first()
        assert(movies.isEmpty())
    }

    @Test
    fun testGetFavoriteMovies() = runTest(testDispatcher) {
        val favoriteMovie = Movie(
            id = 1,
            title = "Favorite Movie",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Favorite description",
            url = "https://example.com",
            isFavorite = true
        )

        val nonFavoriteMovie = Movie(
            id = 2,
            title = "Regular Movie",
            year = 2024,
            genre = "Drama",
            rating = 7.5,
            description = "Regular description",
            url = "https://example2.com",
            isFavorite = false
        )

        movieDao.insert(favoriteMovie)
        movieDao.insert(nonFavoriteMovie)

        val favorites = movieDao.getFavoriteMovies().first()
        assert(favorites.size == 1)
        assert(favorites[0].title == "Favorite Movie")
    }

    @Test
    fun testSearchMoviesByTitle() = runTest(testDispatcher) {
        val movies = listOf(
            Movie(
                id = 1,
                title = "The Matrix",
                year = 1999,
                genre = "Sci-Fi",
                rating = 8.7,
                description = "A hacker movie",
                url = "https://example.com"
            ),
            Movie(
                id = 2,
                title = "The Godfather",
                year = 1972,
                genre = "Crime",
                rating = 9.2,
                description = "A crime movie",
                url = "https://example2.com"
            )
        )

        movieDao.insertAll(movies)

        val results = movieDao.searchMoviesByTitle("Matrix").first()
        assert(results.size == 1)
        assert(results[0].title == "The Matrix")
    }

    @Test
    fun testGetMovieById() = runTest(testDispatcher) {
        val movie = Movie(
            id = 42,
            title = "Specific Movie",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Specific description",
            url = "https://example.com"
        )

        movieDao.insert(movie)

        val retrievedMovie = movieDao.getMovieById(42).first()
        assert(retrievedMovie?.id == 42)
        assert(retrievedMovie?.title == "Specific Movie")
    }
}
