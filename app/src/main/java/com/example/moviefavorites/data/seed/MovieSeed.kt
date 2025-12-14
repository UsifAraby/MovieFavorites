/**
 * Seeded movie data for initial database population.
 */
package com.example.moviefavorites.data.seed

import com.example.moviefavorites.data.model.Movie
import kotlinx.serialization.InternalSerializationApi

/**
 * Object providing a seeded list of movies for initial app launch.
 */
object MovieSeed {
    /**
     * Returns a list of pre-populated movies.
     */
    @OptIn(InternalSerializationApi::class)
    fun getSeededMovies(): List<Movie> = listOf(
        Movie(
            id = 1,
            title = "The Shawshank Redemption",
            year = 1994,
            genre = "Drama",
            rating = 9.3,
            description = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
            url = "https://www.imdb.com/title/tt0111161/",
            posterUrl = "https://via.placeholder.com/150?text=Shawshank"
        ),
        Movie(
            id = 2,
            title = "The Dark Knight",
            year = 2008,
            genre = "Action",
            rating = 9.0,
            description = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological tests.",
            url = "https://www.imdb.com/title/tt0468569/",
            posterUrl = "https://via.placeholder.com/150?text=DarkKnight"
        ),
        Movie(
            id = 3,
            title = "Inception",
            year = 2010,
            genre = "Sci-Fi",
            rating = 8.8,
            description = "A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
            url = "https://www.imdb.com/title/tt1375666/",
            posterUrl = "https://via.placeholder.com/150?text=Inception"
        ),
        Movie(
            id = 4,
            title = "Pulp Fiction",
            year = 1994,
            genre = "Crime",
            rating = 8.9,
            description = "The lives of two mob hitmen, a boxer, a gangster and his wife intertwine in four tales of violence and redemption.",
            url = "https://www.imdb.com/title/tt0110912/",
            posterUrl = "https://via.placeholder.com/150?text=PulpFiction"
        ),
        Movie(
            id = 5,
            title = "Forrest Gump",
            year = 1994,
            genre = "Drama",
            rating = 8.8,
            description = "The presidencies of Kennedy and Johnson, the Vietnam War, and the Watergate scandal unfold from the perspective of an Alabama man.",
            url = "https://www.imdb.com/title/tt0109830/",
            posterUrl = "https://via.placeholder.com/150?text=ForrestGump"
        ),
        Movie(
            id = 6,
            title = "The Matrix",
            year = 1999,
            genre = "Sci-Fi",
            rating = 8.7,
            description = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
            url = "https://www.imdb.com/title/tt0133093/",
            posterUrl = "https://via.placeholder.com/150?text=Matrix"
        ),
        Movie(
            id = 7,
            title = "Interstellar",
            year = 2014,
            genre = "Sci-Fi",
            rating = 8.6,
            description = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
            url = "https://www.imdb.com/title/tt0816692/",
            posterUrl = "https://via.placeholder.com/150?text=Interstellar"
        ),
        Movie(
            id = 8,
            title = "The Godfather",
            year = 1972,
            genre = "Crime",
            rating = 9.2,
            description = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant youngest son.",
            url = "https://www.imdb.com/title/tt0068646/",
            posterUrl = "https://via.placeholder.com/150?text=Godfather"
        ),
        Movie(
            id = 9,
            title = "Fight Club",
            year = 1999,
            genre = "Drama",
            rating = 8.8,
            description = "An insomniac office worker and a devil-may-care soapmaker form an underground fight club that evolves into much more.",
            url = "https://www.imdb.com/title/tt0137523/",
            posterUrl = "https://via.placeholder.com/150?text=FightClub"
        ),
        Movie(
            id = 10,
            title = "Jurassic Park",
            year = 1993,
            genre = "Adventure",
            rating = 8.1,
            description = "A pragmatic paleontologist touring an almost complete theme park is tasked with protecting a couple of kids.",
            url = "https://www.imdb.com/title/tt0107290/",
            posterUrl = "https://via.placeholder.com/150?text=JurassicPark"
        )
    )
}