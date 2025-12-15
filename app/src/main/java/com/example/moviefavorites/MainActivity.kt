
package com.example.moviefavorites

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviefavorites.data.repository.MovieRepositoryImpl
import com.example.moviefavorites.data.room.MovieDatabase
import com.example.moviefavorites.ui.screens.AddMovieDialog
import com.example.moviefavorites.ui.screens.DetailScreen
import com.example.moviefavorites.ui.screens.MainScreen
import com.example.moviefavorites.vm.MovieViewModel
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@OptIn(InternalSerializationApi::class)
class MainActivity : ComponentActivity() {
    private val viewModel: MovieViewModel by lazy {
        val database = MovieDatabase.getInstance(this)
        val repository = MovieRepositoryImpl(database.movieDao())
        MovieViewModel(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var showAddDialog by remember { mutableStateOf(false) }
                    val selectedMovie by viewModel.selectedMovie.collectAsState()

                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        composable("main") {
                            MainScreen(
                                viewModel = viewModel,
                                onMovieClick = { movie ->
                                    viewModel.selectMovie(movie)
                                    navController.navigate("detail")
                                },
                                onAddClick = { showAddDialog = true },
                                onFlutterClick = { launchFlutterDemo() }
                            )
                        }
                        composable("detail") {
                            selectedMovie?.let { movie ->
                                DetailScreen(
                                    movie = movie,
                                    viewModel = viewModel,
                                    onBackClick = {
                                        viewModel.clearSelectedMovie()
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }

                    if (showAddDialog) {
                        AddMovieDialog(
                            viewModel = viewModel,
                            onDismiss = { showAddDialog = false }
                        )
                    }
                }
            }
        }
    }


    private fun launchFlutterDemo() {
        try {
            val flutterPackageName = "com.example.flutter_demo"
            val flutterActivityName = "com.example.flutter_demo.MainActivity"

            android.util.Log.d("MainActivity", "Attempting to launch Flutter app: $flutterPackageName")

            // Create explicit intent using component name
            val intent = Intent().apply {
                component = android.content.ComponentName(flutterPackageName, flutterActivityName)
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            android.util.Log.d("MainActivity", "Intent created, launching Flutter app")

            // Get current movies from ViewModel
            val allMovies = viewModel.allMovies.value
            android.util.Log.d("MainActivity", "Movies to send: ${allMovies.size}")

            // Serialize movies to JSON
            val moviesJson = Json.encodeToString(allMovies)
            android.util.Log.d("MainActivity", "Serialized JSON: ${moviesJson.take(100)}...")

            // Pass data via Intent extras
            intent.putExtra("movies_data", moviesJson)
            intent.putExtra("selected_movie", viewModel.selectedMovie.value?.let {
                Json.encodeToString(it)
            })

            startActivity(intent)
            android.util.Log.d("MainActivity", "Flutter app launched successfully")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error launching Flutter demo", e)
            e.printStackTrace()
        }
    }
}
