/**
 * Main screen showing a list of movies with search functionality.
 */
package com.example.moviefavorites.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviefavorites.data.model.Movie
import com.example.moviefavorites.ui.components.MovieCard
import com.example.moviefavorites.vm.MovieViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Movie) -> Unit,
    onAddClick: () -> Unit,
    onFlutterClick: () -> Unit
) {
    val allMovies = viewModel.allMovies.collectAsState().value
    val searchQuery = viewModel.searchQuery.collectAsState().value
    val isInitialized = viewModel.isInitialized.collectAsState().value
    val genreFilter = viewModel.genreFilter.collectAsState().value
    val showFavoritesOnly = viewModel.showFavoritesOnly.collectAsState().value

    val displayedMovies = viewModel.getDisplayedMovies()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Favorites") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add movie")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!isInitialized) {
                // Loading state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text("Loading movies...", modifier = Modifier.padding(top = 16.dp))
                }
            } else {
                // Search bar
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text("Search by title...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    singleLine = true
                )

                // Filter controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Genre filter dropdown
                    var expanded by remember { mutableStateOf(false) }
                    val genres = allMovies.map { it.genre }.distinct()

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }) {
                        TextField(
                            value = genreFilter ?: "All Genres",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("All Genres") },
                                onClick = {
                                    viewModel.setGenreFilter(null)
                                    expanded = false
                                }
                            )
                            genres.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        viewModel.setGenreFilter(it)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Show favorites switch
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Favorites Only")
                        Switch(
                            checked = showFavoritesOnly,
                            onCheckedChange = { viewModel.setShowFavoritesOnly(it) }
                        )
                    }
                }

                // Flutter demo button
                Button(onClick = onFlutterClick, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text("Open Flutter Demo")
                }

                // Movie list
                if (displayedMovies.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty()) "No movies found" else "No results for '$searchQuery'",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(displayedMovies) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = onMovieClick,
                                onFavoriteClick = { viewModel.toggleFavorite(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
