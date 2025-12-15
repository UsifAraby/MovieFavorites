/// Flutter Demo App for MovieFavorites
///
/// A minimal Flutter application showcasing an interactive movie card widget
/// with state management and the ability to be launched from the Android app
/// via Intent. Demonstrates Flutter basics: StatefulWidget, Material Design,
/// and returning results via Navigator.pop().
///
/// To launch from Android:
///   val intent = packageManager.getLaunchIntentForPackage("com.example.flutter_demo")
///   startActivity(intent)

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'movie_card.dart';

void main() {
  runApp(const MovieFavoritesFlutterDemo());
}

class MovieFavoritesFlutterDemo extends StatelessWidget {
  const MovieFavoritesFlutterDemo({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'MovieFavorites Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.deepPurple,
          brightness: Brightness.light,
        ),
        useMaterial3: true,
      ),
      home: const MovieDemoScreen(),
    );
  }
}

/// Movie data model matching Kotlin app structure
class Movie {
  final int id;
  final String title;
  final int year;
  final String genre;
  final double rating;
  final String description;
  final String url;
  final String posterUrl;
  final bool isFavorite;

  Movie({
    required this.id,
    required this.title,
    required this.year,
    required this.genre,
    required this.rating,
    required this.description,
    required this.url,
    required this.posterUrl,
    required this.isFavorite,
  });

  factory Movie.fromJson(Map<String, dynamic> json) {
    return Movie(
      id: json['id'] ?? 0,
      title: json['title'] ?? 'Unknown',
      year: json['year'] ?? 0,
      genre: json['genre'] ?? 'N/A',
      rating: (json['rating'] ?? 0.0).toDouble(),
      description: json['description'] ?? 'No description',
      url: json['url'] ?? '',
      posterUrl: json['posterUrl'] ?? 'https://via.placeholder.com/150',
      isFavorite: json['isFavorite'] ?? false,
    );
  }
}

/// Main screen displaying received movie data and a demo movie card.
///
/// Shows:
/// - A list of movies from the Android app
/// - A demo movie card with title, rating, and year
/// - A "Close" button that returns to the Android app
class MovieDemoScreen extends StatefulWidget {
  const MovieDemoScreen({Key? key}) : super(key: key);

  @override
  State<MovieDemoScreen> createState() => _MovieDemoScreenState();
}

class _MovieDemoScreenState extends State<MovieDemoScreen> {
  static const platform = MethodChannel('com.example.moviefavorites/movies');
  List<Movie> receivedMovies = [];
  bool isLoading = true;
  String? error;

  @override
  void initState() {
    super.initState();
    _loadMoviesFromIntent();
  }

  /// Receives movie data from the Android app Intent extras
  Future<void> _loadMoviesFromIntent() async {
    try {
      // Get the maps from the native side via Intent extras
      final Map<dynamic, dynamic> args =
          WidgetsBinding.instance.window.onPlatformMessage as dynamic? ?? {};

      // Try to get movie data from platform channel
      final String? moviesJson = await _getIntentExtra('movies_data');

      if (moviesJson != null && moviesJson.isNotEmpty) {
        // Parse the JSON array of movies
        final List<dynamic> jsonList = _parseJsonArray(moviesJson);
        setState(() {
          receivedMovies = jsonList
              .whereType<Map<String, dynamic>>()
              .map((json) => Movie.fromJson(json))
              .toList();
          isLoading = false;
        });
      } else {
        setState(() {
          isLoading = false;
          error = 'No movie data received from Android app';
        });
      }
    } catch (e) {
      setState(() {
        isLoading = false;
        error = 'Error loading movies: $e';
      });
      debugPrint('Error: $e');
    }
  }

  /// Gets an Intent extra from the native Android side
  Future<String?> _getIntentExtra(String key) async {
    try {
      final String? result =
          await platform.invokeMethod<String>('getIntentExtra', {'key': key});
      return result;
    } catch (e) {
      debugPrint('Error getting intent extra: $e');
      return null;
    }
  }

  /// Simple JSON array parser (Flutter doesn't require external dependencies)
  List<dynamic> _parseJsonArray(String jsonString) {
    try {
      // Remove leading/trailing whitespace
      final trimmed = jsonString.trim();

      // If it's an array
      if (trimmed.startsWith('[') && trimmed.endsWith(']')) {
        final content = trimmed.substring(1, trimmed.length - 1);
        if (content.isEmpty) return [];

        List<dynamic> items = [];
        int braceCount = 0;
        int bracketCount = 0;
        int start = 0;

        for (int i = 0; i < content.length; i++) {
          final char = content[i];

          if (char == '{') braceCount++;
          if (char == '}') braceCount--;
          if (char == '[') bracketCount++;
          if (char == ']') bracketCount--;

          if (char == ',' && braceCount == 0 && bracketCount == 0) {
            final item = content.substring(start, i).trim();
            if (item.isNotEmpty) {
              items.add(_parseJsonObject(item));
            }
            start = i + 1;
          }
        }

        // Add the last item
        final lastItem = content.substring(start).trim();
        if (lastItem.isNotEmpty) {
          items.add(_parseJsonObject(lastItem));
        }

        return items;
      }

      // If it's a single object
      if (trimmed.startsWith('{') && trimmed.endsWith('}')) {
        return [_parseJsonObject(trimmed)];
      }

      return [];
    } catch (e) {
      debugPrint('Error parsing JSON: $e');
      return [];
    }
  }

  /// Parses a single JSON object string into a Map
  Map<String, dynamic> _parseJsonObject(String jsonString) {
    final map = <String, dynamic>{};
    try {
      final trimmed = jsonString.trim();
      if (!trimmed.startsWith('{') || !trimmed.endsWith('}')) {
        return map;
      }

      final content = trimmed.substring(1, trimmed.length - 1);
      if (content.isEmpty) return map;

      int quoteCount = 0;
      int braceCount = 0;
      int bracketCount = 0;
      int start = 0;

      for (int i = 0; i < content.length; i++) {
        final char = content[i];

        if (char == '"' && (i == 0 || content[i - 1] != '\\')) {
          quoteCount++;
        }

        if (quoteCount % 2 == 0) {
          if (char == '{') braceCount++;
          if (char == '}') braceCount--;
          if (char == '[') bracketCount++;
          if (char == ']') bracketCount--;

          if (char == ',' && braceCount == 0 && bracketCount == 0) {
            final pair = content.substring(start, i).trim();
            _addKeyValue(map, pair);
            start = i + 1;
          }
        }
      }

      // Add the last pair
      final lastPair = content.substring(start).trim();
      _addKeyValue(map, lastPair);

      return map;
    } catch (e) {
      debugPrint('Error parsing JSON object: $e');
      return map;
    }
  }

  /// Parses and adds a key-value pair to the map
  void _addKeyValue(Map<String, dynamic> map, String pair) {
    if (pair.isEmpty) return;

    final colonIndex = pair.indexOf(':');
    if (colonIndex == -1) return;

    String key = pair.substring(0, colonIndex).trim();
    String value = pair.substring(colonIndex + 1).trim();

    // Remove quotes from key
    if (key.startsWith('"') && key.endsWith('"')) {
      key = key.substring(1, key.length - 1);
    }

    // Parse value
    if (value == 'true') {
      map[key] = true;
    } else if (value == 'false') {
      map[key] = false;
    } else if (value == 'null') {
      map[key] = null;
    } else if (value.startsWith('"') && value.endsWith('"')) {
      map[key] = value.substring(1, value.length - 1);
    } else if (value.startsWith('[') && value.endsWith(']')) {
      // For now, just store as string
      map[key] = value;
    } else {
      // Try to parse as number
      try {
        if (value.contains('.')) {
          map[key] = double.parse(value);
        } else {
          map[key] = int.parse(value);
        }
      } catch (e) {
        map[key] = value;
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('MovieFavorites Flutter Demo'),
        elevation: 0,
      ),
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : error != null
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(Icons.error, size: 48, color: Colors.red),
                      const SizedBox(height: 16),
                      Text(error!),
                      const SizedBox(height: 24),
                      ElevatedButton(
                        onPressed: () {
                          Navigator.of(context).pop('Demo closed');
                        },
                        child: const Text('Return to Android App'),
                      ),
                    ],
                  ),
                )
              : SingleChildScrollView(
                  child: Column(
                    children: [
                      const SizedBox(height: 24),
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 16),
                        child: Card(
                          elevation: 4,
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: const MovieCardWidget(
                            title: 'Inception',
                            rating: 8.8,
                            year: 2010,
                            description:
                                'A skilled thief who steals corporate secrets through the use of dream-sharing technology.',
                            posterUrl: 'https://via.placeholder.com/150',
                          ),
                        ),
                      ),
                      const SizedBox(height: 24),
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 16),
                        child: Text(
                          'Movies from Android App (${receivedMovies.length})',
                          style: Theme.of(context).textTheme.titleLarge,
                        ),
                      ),
                      const SizedBox(height: 16),
                      if (receivedMovies.isEmpty)
                        Padding(
                          padding: const EdgeInsets.all(16),
                          child: Card(
                            color: Colors.orange.shade50,
                            child: Padding(
                              padding: const EdgeInsets.all(16),
                              child: Text(
                                'No movies received from Android app. Make sure to pass movie data when launching.',
                                style: TextStyle(color: Colors.orange.shade900),
                              ),
                            ),
                          ),
                        )
                      else
                        ListView.builder(
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          itemCount: receivedMovies.length,
                          itemBuilder: (context, index) {
                            final movie = receivedMovies[index];
                            return Padding(
                              padding:
                                  const EdgeInsets.symmetric(horizontal: 16),
                              child: Card(
                                margin: const EdgeInsets.only(bottom: 12),
                                child: ListTile(
                                  leading: Container(
                                    width: 50,
                                    height: 75,
                                    decoration: BoxDecoration(
                                      color: Colors.grey.shade300,
                                      borderRadius: BorderRadius.circular(4),
                                    ),
                                    child: Center(
                                      child: Icon(
                                        Icons.movie,
                                        color: Colors.grey.shade600,
                                      ),
                                    ),
                                  ),
                                  title: Text(
                                    movie.title,
                                    style: const TextStyle(
                                        fontWeight: FontWeight.bold),
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                  ),
                                  subtitle: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        '${movie.year} • ${movie.genre}',
                                        style: TextStyle(
                                            color: Colors.grey.shade600),
                                      ),
                                      Row(
                                        children: [
                                          Icon(
                                            Icons.star,
                                            size: 16,
                                            color: Colors.amber.shade600,
                                          ),
                                          const SizedBox(width: 4),
                                          Text(movie.rating.toStringAsFixed(1)),
                                        ],
                                      ),
                                    ],
                                  ),
                                  trailing: Icon(
                                    movie.isFavorite
                                        ? Icons.favorite
                                        : Icons.favorite_border,
                                    color: Colors.red,
                                  ),
                                ),
                              ),
                            );
                          },
                        ),
                      const SizedBox(height: 24),
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 16),
                        child: SizedBox(
                          width: double.infinity,
                          child: ElevatedButton.icon(
                            onPressed: () {
                              Navigator.of(context).pop('Demo closed');
                            },
                            icon: const Icon(Icons.close),
                            label: const Text('Close Flutter Demo'),
                          ),
                        ),
                      ),
                      const SizedBox(height: 24),
                    ],
                  ),
                ),
    );
  }
}
