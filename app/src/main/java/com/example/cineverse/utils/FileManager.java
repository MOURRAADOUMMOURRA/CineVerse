package com.example.cineverse.utils;

import android.content.Context;

import com.example.cineverse.models.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class FileManager {

    private Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    // Sauvegarder la collection
    public void saveCollection(ArrayList<Movie> movies) {
        try {
            JSONArray array = new JSONArray();

            for (Movie movie : movies) {
                JSONObject obj = new JSONObject();
                obj.put("id", movie.getId());
                obj.put("title", movie.getTitle());
                obj.put("overview", movie.getOverview());
                obj.put("posterPath", movie.getPosterPath());
                obj.put("rating", movie.getRating());
                obj.put("releaseDate", movie.getReleaseDate());
                obj.put("status", movie.getStatus());
                obj.put("personalRating", movie.getPersonalRating());
                obj.put("personalNote", movie.getPersonalNote());
                obj.put("watchDate", movie.getWatchDate());
                array.put(obj);
            }

            FileOutputStream fos = context.openFileOutput(
                    Constants.COLLECTION_FILE,
                    Context.MODE_PRIVATE
            );
            fos.write(array.toString().getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Charger la collection
    public ArrayList<Movie> loadCollection() {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(
                    Constants.COLLECTION_FILE
            );
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fis)
            );
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            fis.close();

            JSONArray array = new JSONArray(sb.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Movie movie = new Movie(
                        obj.getInt("id"),
                        obj.getString("title"),
                        obj.getString("overview"),
                        obj.getString("posterPath"),
                        obj.getDouble("rating"),
                        obj.getString("releaseDate")
                );
                movie.setStatus(obj.getString("status"));
                movie.setPersonalRating(
                        (float) obj.getDouble("personalRating")
                );
                movie.setPersonalNote(obj.getString("personalNote"));
                movie.setWatchDate(obj.getString("watchDate"));
                movies.add(movie);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    // Ajouter un film
    public void addMovie(Movie movie) {
        ArrayList<Movie> movies = loadCollection();
        // Éviter les doublons
        for (Movie m : movies) {
            if (m.getId() == movie.getId()) return;
        }
        movies.add(movie);
        saveCollection(movies);
    }

    // Supprimer un film
    public void removeMovie(int movieId) {
        ArrayList<Movie> movies = loadCollection();
        movies.removeIf(m -> m.getId() == movieId);
        saveCollection(movies);
    }
}