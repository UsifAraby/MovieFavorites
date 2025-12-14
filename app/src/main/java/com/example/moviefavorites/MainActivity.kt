
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
import com.example.moviefavorites.ui.screens.DetailScreen
import com.example.moviefavorites.vm.MovieViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//test
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
                       // for flutter to be added later
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

                   //add movie

                }
            }
        }
    }

}
