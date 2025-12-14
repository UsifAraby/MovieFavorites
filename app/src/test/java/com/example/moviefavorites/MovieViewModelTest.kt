/**
 * Unit tests for MovieViewModel using Mockito.
 */
package com.example.moviefavorites.vm

import com.example.moviefavorites.data.model.Movie
import com.example.moviefavorites.data.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.InternalSerializationApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class, InternalSerializationApi::class)
class MovieViewModelTest {
    @Mock
    private lateinit var mockRepository: MovieRepository

    private lateinit var viewModel: MovieViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Mock repository methods
        whenever(mockRepository.getAllMovies()).thenReturn(flowOf(emptyList()))
        whenever(mockRepository.getFavoriteMovies()).thenReturn(flowOf(emptyList()))
        whenever(mockRepository.searchMoviesByTitle("")).thenReturn(flowOf(emptyList()))

        viewModel = MovieViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testToggleFavorite_callsRepositoryUpdate() = runTest {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Test description",
            url = "https://example.com",
            isFavorite = false
        )

        viewModel.toggleFavorite(movie)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify repository update was called with toggled favorite status
        verify(mockRepository).toggleFavorite(movie)
    }

    @Test
    fun testMarkFavorite_updatesMovieAsFavorite() = runTest {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Test description",
            url = "https://example.com",
            isFavorite = false
        )

        viewModel.markFavorite(movie)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify repository update was called
        verify(mockRepository).updateMovie(movie.copy(isFavorite = true))
    }

    @Test
    fun testAddMovie_callsRepositoryInsert() = runTest {
        viewModel.addMovie(
            title = "New Movie",
            year = 2024,
            genre = "Drama",
            rating = 7.5,
            url = "https://example.com",
            description = "New movie description"
        )

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify repository insert was called
        verify(mockRepository).insertMovie(
            org.mockito.kotlin.argThat {
                this.title == "New Movie" &&
                        this.year == 2024 &&
                        this.genre == "Drama" &&
                        this.rating == 7.5 &&
                        this.url == "https://example.com" &&
                        this.description == "New movie description"
            }
        )
    }

    @Test
    fun testUpdateMovie_callsRepositoryUpdate() = runTest {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Test description",
            url = "https://example.com"
        )

        viewModel.updateMovie(movie)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify repository update was called
        verify(mockRepository).updateMovie(movie)
    }

    @Test
    fun testDeleteMovie_callsRepositoryDelete() = runTest {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Test description",
            url = "https://example.com"
        )

        viewModel.deleteMovie(movie)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify repository delete was called
        verify(mockRepository).deleteMovie(movie)
    }

    @Test
    fun testUpdateSearchQuery_performsSearch() = runTest {
        val mockMovies = listOf(
            Movie(
                id = 1,
                title = "The Matrix",
                year = 1999,
                genre = "Sci-Fi",
                rating = 8.7,
                description = "A hacker movie",
                url = "https://example.com"
            )
        )

        whenever(mockRepository.searchMoviesByTitle("Matrix"))
            .thenReturn(flowOf(mockMovies))

        viewModel.updateSearchQuery("Matrix")

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify search was performed
        verify(mockRepository).searchMoviesByTitle("Matrix")
    }

    @Test
    fun testSelectMovie_storesSelectedMovie() {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Test description",
            url = "https://example.com"
        )

        viewModel.selectMovie(movie)

        assert(viewModel.selectedMovie.value == movie)
    }

    @Test
    fun testClearSelectedMovie_resetsSelection() {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            year = 2024,
            genre = "Action",
            rating = 8.5,
            description = "Test description",
            url = "https://example.com"
        )

        viewModel.selectMovie(movie)
        viewModel.clearSelectedMovie()

        assert(viewModel.selectedMovie.value == null)
    }
}
