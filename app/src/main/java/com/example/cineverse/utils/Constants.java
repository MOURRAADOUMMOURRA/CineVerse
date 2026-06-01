package com.example.cineverse.utils;

public class Constants {

    // Clé API TMDB
    public static final String API_KEY =
            "af26ee7b10f6562af565df9231173190";

    // URL de base TMDB
    public static final String BASE_URL =
            "https://api.themoviedb.org/3/";

    // URL des affiches
    public static final String IMAGE_URL =
            "https://image.tmdb.org/t/p/w500";

    // Genre Action = 28
    public static final int GENRE_ACTION = 28;

    // Clés pour Intent
    public static final String KEY_MOVIE_ID = "movie_id";
    public static final String KEY_MOVIE_TITLE = "movie_title";
    public static final String KEY_MOVIE_POSTER = "movie_poster";
    public static final String KEY_MOVIE_OVERVIEW = "movie_overview";
    public static final String KEY_MOVIE_RATING = "movie_rating";
    public static final String KEY_MOVIE_DATE = "movie_date";

    // Clés SharedPreferences
    public static final String PREF_NAME = "CineVersePrefs";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_THEME = "theme";

    // Fichier collection locale
    public static final String COLLECTION_FILE = "collection.json";
}
