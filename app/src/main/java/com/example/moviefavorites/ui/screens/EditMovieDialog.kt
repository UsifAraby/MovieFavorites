/**
 * Dialog for editing an existing movie.
 */
package com.example.moviefavorites.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviefavorites.data.model.Movie
import com.example.moviefavorites.vm.MovieViewModel


@Composable
fun EditMovieDialog(
    movie: Movie,
    viewModel: MovieViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(movie.title) }
    var year by remember { mutableStateOf(movie.year.toString()) }
    var genre by remember { mutableStateOf(movie.genre) }
    var rating by remember { mutableStateOf(movie.rating.toString()) }
    var url by remember { mutableStateOf(movie.url) }
    var description by remember { mutableStateOf(movie.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Movie") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = rating,
                    onValueChange = { rating = it },
                    label = { Text("Rating (0-10)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Official URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && year.isNotBlank() && rating.isNotBlank() && url.isNotBlank()) {
                        viewModel.updateMovie(
                            movie.copy(
                                title = title,
                                year = year.toIntOrNull() ?: movie.year,
                                genre = genre.ifBlank { "Unknown" },
                                rating = rating.toDoubleOrNull() ?: movie.rating,
                                url = url,
                                description = description
                            )
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
