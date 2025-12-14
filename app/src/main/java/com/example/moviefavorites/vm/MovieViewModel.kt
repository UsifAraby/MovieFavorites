/**
 * ViewModel for managing movie-related UI state and business logic.
 */
package com.example.moviefavorites.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviefavorites.data.model.Movie
import com.example.moviefavorites.data.repository.MovieRepository
import com.example.moviefavorites.data.seed.MovieSeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class MovieViewModel(
    private val repository: MovieRepository
) : ViewModel() {
    private val _allMovies = repository.getAllMovies()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    val allMovies: StateFlow<List<Movie>> = _allMovies

    private val _favoriteMovies = repository.getFavoriteMovies()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> = _searchResults.asStateFlow()

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _genreFilter = MutableStateFlow<String?>(null)
    val genreFilter: StateFlow<String?> = _genreFilter.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    init {
        initializeDatabase()
    }


    private fun initializeDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Insert seeded movies on first launch
                repository.insertMovies(MovieSeed.getSeededMovies())
                _isInitialized.value = true
            } catch (e: Exception) {
                // Database already populated or error occurred
                _isInitialized.value = true
            }
        }
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isEmpty()) {
                _searchResults.value = emptyList()
            } else {
                try {
                    repository.searchMoviesByTitle(query).collect { results ->
                        _searchResults.value = results
                    }
                } catch (e: Exception) {
                    _searchResults.value = emptyList()
                }
            }
        }
    }


    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun clearSelectedMovie() {
        _selectedMovie.value = null
    }


    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavorite(movie)
        }
    }


    fun markFavorite(movie: Movie) {
        if (!movie.isFavorite) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateMovie(movie.copy(isFavorite = true))
            }
        }
    }


    fun addMovie(
        title: String,
        year: Int,
        genre: String,
        rating: Double,
        url: String,
        description: String
    ) {
        val movie = Movie(
            title = title,
            year = year,
            genre = genre,
            rating = rating,
            url = url,
            description = description,
            posterUrl = "https://via.placeholder.com/150?text=${title.take(10)}"
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMovie(movie)
        }
    }


    fun updateMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMovie(movie)
        }
    }


    fun deleteMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovie(movie)
        }
    }


    fun getDisplayedMovies(): List<Movie> {
        val moviesToFilter = if (_searchQuery.value.isNotEmpty()) {
            _searchResults.value
        } else {
            _allMovies.value
        }

        return moviesToFilter.filter { movie ->
            val genreMatch = _genreFilter.value?.let { movie.genre == it } ?: true
            val favoriteMatch = if (_showFavoritesOnly.value) movie.isFavorite else true
            genreMatch && favoriteMatch
        }
    }


    fun setGenreFilter(genre: String?) {
        _genreFilter.value = genre
    }


    fun setShowFavoritesOnly(showFavorites: Boolean) {
        _showFavoritesOnly.value = showFavorites
    }
}
