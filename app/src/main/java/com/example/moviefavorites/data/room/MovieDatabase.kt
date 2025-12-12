/**
 * Room database configuration for Movie entities.
 */
package com.example.moviefavorites.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviefavorites.data.model.Movie

/**
 * Room Database configuration for the Movie entity.
 * Version: 1 - Initial database schema with Movie entity.
 */
@Database(
    entities = [Movie::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    /**
     * Provides access to the MovieDao.
     */
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var instance: MovieDatabase? = null

        /**
         * Gets or creates the singleton instance of MovieDatabase.
         * Uses double-checked locking for thread-safe lazy initialization.
         *
         * @param context The application context.
         * @return The singleton MovieDatabase instance.
         */
        fun getInstance(context: Context): MovieDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_database"
                ).build().also { instance = it }
            }
        }
    }
}