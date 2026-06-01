package com.example.cineverse.activities;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cineverse.R;
import com.example.cineverse.adapters.SearchAdapter;
import com.example.cineverse.models.Movie;
import com.example.cineverse.network.VolleyClient;
import com.example.cineverse.utils.Constants;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private static final int SEARCH_DELAY = 500;

    private RecyclerView  recyclerView;
    private SearchAdapter adapter;
    // ✅ List au lieu de ArrayList
    private List<Movie>   movieList;
    private TextView      textResultCount;
    private ProgressBar   progressBar;
    private View          layoutEmpty;
    private View          layoutRoot;

    private final Handler searchHandler =
            new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Toolbar
        MaterialToolbar toolbar =
                findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        // Vues
        layoutRoot      = findViewById(android.R.id.content);
        recyclerView    = findViewById(R.id.recyclerViewSearch);
        textResultCount = findViewById(R.id.textResultCount);
        progressBar     = findViewById(R.id.progressBar);
        layoutEmpty     = findViewById(R.id.layoutEmpty);

        // ✅ Initialiser liste vide
        movieList = new ArrayList<>();

        // RecyclerView
        LinearLayoutManager llm =
                new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemViewCacheSize(20);

        // ✅ Adapter initialisé avec liste vide
        adapter = new SearchAdapter(
                movieList, movie -> {
            Intent intent = new Intent(
                    SearchActivity.this,
                    DetailActivity.class);
            intent.putExtra(Constants.KEY_MOVIE_ID,
                    movie.getId());
            intent.putExtra(Constants.KEY_MOVIE_TITLE,
                    movie.getTitle());
            intent.putExtra(Constants.KEY_MOVIE_POSTER,
                    movie.getPosterPath());
            intent.putExtra(Constants.KEY_MOVIE_OVERVIEW,
                    movie.getOverview());
            intent.putExtra(Constants.KEY_MOVIE_RATING,
                    movie.getRating());
            intent.putExtra(Constants.KEY_MOVIE_DATE,
                    movie.getReleaseDate());
            startActivity(intent);
            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out);
        });

        recyclerView.setAdapter(adapter);

        // ✅ État initial correct
        layoutEmpty.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        // SearchView
        SearchView searchView =
                findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(
                            String query) {
                        searchView.clearFocus();
                        triggerSearch(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(
                            String newText) {
                        // Annuler recherche précédente
                        if (searchRunnable != null) {
                            searchHandler
                                    .removeCallbacks(
                                            searchRunnable);
                        }
                        // ✅ Vider si texte effacé
                        if (newText.trim().isEmpty()) {
                            loadPopularMovies();
                            return true;
                        }
                        if (newText.length() >= 2) {
                            searchRunnable = () ->
                                    triggerSearch(newText);
                            searchHandler.postDelayed(
                                    searchRunnable,
                                    SEARCH_DELAY);
                        }
                        return true;
                    }
                });

        // ✅ Charger films populaires au démarrage
        loadPopularMovies();
    }

    // ─── Afficher / cacher loader ─────────────────
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    // ─── Afficher état vide ───────────────────────
    private void showEmpty(boolean show, String query) {
        if (show) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            TextView textEmpty = layoutEmpty
                    .findViewById(R.id.textEmptyMessage);
            if (textEmpty != null) {
                textEmpty.setText(
                        query.isEmpty()
                                ? "Aucun film trouvé"
                                : "Aucun résultat pour \""
                                + query + "\""
                );
            }
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // ─── Déclencher recherche ─────────────────────
    private void triggerSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        if (isConnected()) {
            searchMovies(query.trim());
        } else {
            showNoConnectionSnackbar();
        }
    }

    // ─── Vérification réseau moderne ─────────────
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(
                        CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities caps =
                cm.getNetworkCapabilities(network);
        return caps != null
                && caps.hasCapability(
                NetworkCapabilities
                        .NET_CAPABILITY_INTERNET);
    }

    private void showNoConnectionSnackbar() {
        Snackbar.make(
                layoutRoot,
                "Pas de connexion internet",
                Snackbar.LENGTH_LONG
        ).setBackgroundTint(
                getColor(R.color.card_elevated)
        ).setTextColor(
                getColor(R.color.text_primary)
        ).show();
    }

    // ─── Recherche TMDB ───────────────────────────
    private void searchMovies(String query) {
        // ✅ Vider et notifier AVANT le chargement
        movieList = new ArrayList<>();
        adapter.updateList(movieList);
        showLoading(true);
        textResultCount.setText("Recherche...");

        String url = Constants.BASE_URL
                + "search/movie?api_key="
                + Constants.API_KEY
                + "&query="
                + query.replace(" ", "%20")
                + "&language=fr-FR";

        Log.d(TAG, "Search URL: " + url);

        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.GET,
                        url, null,
                        response -> {
                            showLoading(false);
                            try {
                                JSONArray results =
                                        response.getJSONArray(
                                                "results");
                                int count =
                                        results.length();

                                // ✅ Construire
                                //    nouvelle liste
                                List<Movie> newList =
                                        new ArrayList<>();
                                for (int i = 0;
                                     i < count; i++) {
                                    newList.add(parseMovie(
                                            results
                                                    .getJSONObject(i)
                                    ));
                                }

                                // ✅ Mettre à jour
                                //    via DiffUtil
                                movieList = newList;
                                adapter.updateList(
                                        movieList);

                                if (count == 0) {
                                    showEmpty(true, query);
                                    textResultCount
                                            .setText("");
                                } else {
                                    showEmpty(false, query);
                                    textResultCount.setText(
                                            count
                                                    + " résultat"
                                                    + (count > 1
                                                    ? "s" : "")
                                    );
                                }

                            } catch (Exception e) {
                                Log.e(TAG,
                                        "Parse error: "
                                                + e.getMessage());
                                showEmpty(true, query);
                            }
                        },
                        error -> {
                            showLoading(false);
                            showNoConnectionSnackbar();
                            textResultCount.setText(
                                    "Erreur réseau");
                            Log.e(TAG,
                                    "Network error: "
                                            + error.getMessage());
                        });

        VolleyClient.getInstance(this)
                .getRequestQueue()
                .add(request);
    }

    // ─── Films populaires par défaut ─────────────
    private void loadPopularMovies() {
        movieList = new ArrayList<>();
        adapter.updateList(movieList);
        showLoading(true);
        textResultCount.setText("Films populaires");

        String url = Constants.BASE_URL
                + "movie/popular?api_key="
                + Constants.API_KEY
                + "&language=fr-FR&page=1";

        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.GET,
                        url, null,
                        response -> {
                            showLoading(false);
                            try {
                                JSONArray results =
                                        response.getJSONArray(
                                                "results");

                                List<Movie> newList =
                                        new ArrayList<>();
                                for (int i = 0;
                                     i < results.length();
                                     i++) {
                                    newList.add(parseMovie(
                                            results
                                                    .getJSONObject(i)
                                    ));
                                }

                                // ✅ Update via DiffUtil
                                movieList = newList;
                                adapter.updateList(
                                        movieList);

                                if (movieList.isEmpty()) {
                                    showEmpty(true, "");
                                } else {
                                    showEmpty(false, "");
                                    textResultCount.setText(
                                            "Films populaires"
                                    );
                                }

                            } catch (Exception e) {
                                Log.e(TAG,
                                        "Parse error: "
                                                + e.getMessage());
                                showEmpty(true, "");
                            }
                        },
                        error -> {
                            showLoading(false);
                            Log.e(TAG,
                                    "Error: "
                                            + error.getMessage());
                            showNoConnectionSnackbar();
                        });

        VolleyClient.getInstance(this)
                .getRequestQueue()
                .add(request);
    }

    // ─── Parser film depuis JSON ──────────────────
    private Movie parseMovie(JSONObject obj) {
        try {
            return new Movie(
                    obj.optInt("id", 0),
                    obj.optString("title",
                            "Titre inconnu"),
                    obj.optString("overview", ""),
                    obj.optString("poster_path", ""),
                    obj.optDouble("vote_average", 0.0),
                    obj.optString("release_date", "")
            );
        } catch (Exception e) {
            return new Movie(0, "Erreur",
                    "", "", 0.0, "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchRunnable != null) {
            searchHandler
                    .removeCallbacks(searchRunnable);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out);
        return true;
    }
}