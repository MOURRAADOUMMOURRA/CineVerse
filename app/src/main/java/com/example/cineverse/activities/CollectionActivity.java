package com.example.cineverse.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cineverse.R;
import com.example.cineverse.adapters.MovieAdapter;
import com.example.cineverse.models.Movie;
import com.example.cineverse.utils.Constants;
import com.example.cineverse.utils.FileManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    private RecyclerView         recyclerView;
    private MovieAdapter         adapter;
    private List<Movie>          movieList;
    private FloatingActionButton fabSearch;
    private FileManager          fileManager;
    private View                 layoutEmpty;
    private Movie                selectedMovie;
    private int                  selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        // Toolbar
        MaterialToolbar toolbar =
                findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Vues
        recyclerView = findViewById(R.id.recyclerViewMovies);
        fabSearch    = findViewById(R.id.fabSearch);
        layoutEmpty  = findViewById(R.id.layoutEmpty);
        fileManager  = new FileManager(this);
        movieList    = new ArrayList<>();

        // RecyclerView optimisé
        LinearLayoutManager llm =
                new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemViewCacheSize(20);

        // Adapter
        adapter = new MovieAdapter(movieList,
                new MovieAdapter.OnMovieClickListener() {
                    @Override
                    public void onMovieClick(
                            Movie movie, int position) {
                        Intent intent = new Intent(
                                CollectionActivity.this,
                                DetailActivity.class);
                        intent.putExtra(
                                Constants.KEY_MOVIE_ID,
                                movie.getId());
                        intent.putExtra(
                                Constants.KEY_MOVIE_TITLE,
                                movie.getTitle());
                        intent.putExtra(
                                Constants.KEY_MOVIE_POSTER,
                                movie.getPosterPath());
                        intent.putExtra(
                                Constants.KEY_MOVIE_OVERVIEW,
                                movie.getOverview());
                        intent.putExtra(
                                Constants.KEY_MOVIE_RATING,
                                movie.getRating());
                        intent.putExtra(
                                Constants.KEY_MOVIE_DATE,
                                movie.getReleaseDate());
                        startActivity(intent);
                        overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out);
                    }

                    @Override
                    public void onMovieLongClick(
                            Movie movie, int position,
                            View view) {
                        selectedMovie    = movie;
                        selectedPosition = position;
                        registerForContextMenu(view);
                        openContextMenu(view);
                    }
                });

        recyclerView.setAdapter(adapter);

        // FAB → SearchActivity
        fabSearch.setOnClickListener(v -> {
            startActivity(new Intent(
                    CollectionActivity.this,
                    SearchActivity.class));
            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger la collection depuis le fichier
        movieList = fileManager.loadCollection();
        adapter.updateList(movieList);
        updateEmptyState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fileManager.saveCollection(
                new ArrayList<>(movieList));
    }

    // Afficher / cacher état vide
    private void updateEmptyState() {
        boolean empty = movieList.isEmpty();
        layoutEmpty.setVisibility(
                empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(
                empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.menu_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            startActivity(new Intent(
                    this, SearchActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(
                    this, SettingsActivity.class));
            return true;
        } else if (id == R.id.filter_all) {
            // Réafficher toute la liste
            adapter.updateList(movieList);
            updateEmptyState();
            return true;
        } else if (id == R.id.filter_a_voir) {
            filterByStatus("À voir");
            return true;
        } else if (id == R.id.filter_en_cours) {
            filterByStatus("En cours");
            return true;
        } else if (id == R.id.filter_vu) {
            filterByStatus("Vu");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(
            android.view.ContextMenu menu,
            View v,
            android.view.ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu, v, info);
        getMenuInflater().inflate(
                R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.context_modifier_statut) {
            showStatusDialog();
            return true;
        } else if (id == R.id.context_modifier_note) {
            showRatingDialog();
            return true;
        } else if (id == R.id.context_supprimer) {
            showDeleteDialog();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void showStatusDialog() {
        String[] statuts = {"À voir", "En cours", "Vu"};
        new AlertDialog.Builder(this)
                .setTitle("Modifier le statut")
                .setItems(statuts, (dialog, which) -> {
                    selectedMovie.setStatus(statuts[which]);
                    fileManager.saveCollection(
                            new ArrayList<>(movieList));
                    adapter.notifyItemChanged(
                            selectedPosition);
                    Snackbar.make(
                            recyclerView,
                            "Statut mis à jour",
                            Snackbar.LENGTH_SHORT
                    ).show();
                })
                .show();
    }

    private void showRatingDialog() {
        String[] notes = {
                "★ 1", "★★ 2", "★★★ 3",
                "★★★★ 4", "★★★★★ 5"
        };
        new AlertDialog.Builder(this)
                .setTitle("Modifier la note")
                .setItems(notes, (dialog, which) -> {
                    selectedMovie.setPersonalRating(
                            which + 1);
                    fileManager.saveCollection(
                            new ArrayList<>(movieList));
                    adapter.notifyItemChanged(
                            selectedPosition);
                    Snackbar.make(
                            recyclerView,
                            "Note mise à jour",
                            Snackbar.LENGTH_SHORT
                    ).show();
                })
                .show();
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Supprimer \""
                        + selectedMovie.getTitle()
                        + "\" de la collection ?")
                .setPositiveButton("Supprimer",
                        (dialog, which) -> {
                            // Supprimer du fichier
                            fileManager.removeMovie(
                                    selectedMovie.getId());
                            // Supprimer de la liste
                            movieList.remove(selectedPosition);
                            adapter.notifyItemRemoved(
                                    selectedPosition);
                            updateEmptyState();
                            Snackbar.make(
                                    recyclerView,
                                    "Film supprimé",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void filterByStatus(String status) {
        List<Movie> filtered = new ArrayList<>();
        for (Movie m : movieList) {
            if (status.equals(m.getStatus())) {
                filtered.add(m);
            }
        }
        adapter.updateList(filtered);
        boolean empty = filtered.isEmpty();
        layoutEmpty.setVisibility(
                empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(
                empty ? View.GONE : View.VISIBLE);
    }
}