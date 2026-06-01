package com.example.cineverse.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.cineverse.R;
import com.example.cineverse.models.Movie;
import com.example.cineverse.utils.Constants;
import com.example.cineverse.utils.FileManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {

    private TextView textTitle, textRating,
            textDate, textOverview, textSelectedDate;
    private Spinner spinnerStatus;
    private RatingBar ratingBarPersonal;
    private EditText editTextNote;
    private MaterialButton buttonAdd, buttonDate;
    private FileManager fileManager;
    private String selectedDate = "";

    // Données du film
    private int    movieId;
    private String title, poster, overview, date;
    private double rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Toolbar + CollapsingToolbar
        MaterialToolbar toolbar =
                findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbar =
                findViewById(R.id.collapsingToolbar);

        // Vues
        android.widget.ImageView imagePoster =
                findViewById(R.id.imagePoster);
        textTitle        = findViewById(R.id.textTitle);
        textRating       = findViewById(R.id.textRating);
        textDate         = findViewById(R.id.textDate);
        textOverview     = findViewById(R.id.textOverview);
        textSelectedDate = findViewById(R.id.textSelectedDate);
        spinnerStatus    = findViewById(R.id.spinnerStatus);
        ratingBarPersonal= findViewById(R.id.ratingBarPersonal);
        editTextNote     = findViewById(R.id.editTextNote);
        buttonAdd        = findViewById(R.id.buttonAdd);
        buttonDate       = findViewById(R.id.buttonDate);

        fileManager = new FileManager(this);

        // Récupérer données Intent
        movieId  = getIntent().getIntExtra(
                Constants.KEY_MOVIE_ID, 0);
        title    = getIntent().getStringExtra(
                Constants.KEY_MOVIE_TITLE);
        poster   = getIntent().getStringExtra(
                Constants.KEY_MOVIE_POSTER);
        overview = getIntent().getStringExtra(
                Constants.KEY_MOVIE_OVERVIEW);
        rating   = getIntent().getDoubleExtra(
                Constants.KEY_MOVIE_RATING, 0.0);
        date     = getIntent().getStringExtra(
                Constants.KEY_MOVIE_DATE);

        // Afficher données
        textTitle.setText(title);
        textRating.setText(
                String.format("%.1f/10", rating));
        textDate.setText(formatDate(date));
        textOverview.setText(
                overview != null && !overview.isEmpty()
                        ? overview
                        : "Synopsis non disponible.");

        // Titre dans CollapsingToolbar
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(title);
        }

        // Charger affiche avec transition
        if (poster != null && !poster.isEmpty()) {
            Glide.with(this)
                    .load(Constants.IMAGE_URL + poster)
                    .transition(
                            DrawableTransitionOptions
                                    .withCrossFade(300)
                    )
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(imagePoster);
        }

        // Spinner statut
        String[] statuts = {"À voir", "En cours", "Vu"};
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        statuts
                );
        spinnerAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item
        );
        spinnerStatus.setAdapter(spinnerAdapter);

        // Vérifier si déjà dans la collection
        checkIfAlreadyInCollection();

        // DatePicker
        buttonDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        selectedDate = day + "/"
                                + (month + 1) + "/"
                                + year;
                        textSelectedDate.setText(
                                selectedDate
                        );
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Bouton Ajouter
        buttonAdd.setOnClickListener(v -> {
            addToCollection();
        });
    }

    // Vérifier si le film est déjà en collection
    private void checkIfAlreadyInCollection() {
        FileManager fm = new FileManager(this);
        for (Movie m : fm.loadCollection()) {
            if (m.getId() == movieId) {
                buttonAdd.setText("Déjà dans la collection");
                buttonAdd.setEnabled(false);
                buttonAdd.setAlpha(0.6f);

                // Restaurer ses données
                spinnerStatus.setSelection(
                        getStatusIndex(m.getStatus())
                );
                ratingBarPersonal.setRating(
                        m.getPersonalRating()
                );
                editTextNote.setText(m.getPersonalNote());
                selectedDate = m.getWatchDate();
                if (!selectedDate.isEmpty()) {
                    textSelectedDate.setText(selectedDate);
                }
                return;
            }
        }
    }

    // Index du statut pour le Spinner
    private int getStatusIndex(String status) {
        switch (status) {
            case "En cours": return 1;
            case "Vu":       return 2;
            default:         return 0;
        }
    }

    // Ajouter à la collection
    private void addToCollection() {
        Movie movie = new Movie(
                movieId, title, overview,
                poster, rating, date
        );

        String status = spinnerStatus
                .getSelectedItem().toString();
        float personalRating =
                ratingBarPersonal.getRating();
        String note = editTextNote
                .getText().toString().trim();

        movie.setStatus(status);
        movie.setPersonalRating(personalRating);
        movie.setPersonalNote(note);
        movie.setWatchDate(selectedDate);

        fileManager.addMovie(movie);

        // Feedback visuel
        Snackbar.make(
                findViewById(android.R.id.content),
                "\"" + title + "\" ajouté à ta collection !",
                Snackbar.LENGTH_LONG
        ).setAction("Voir", v -> {
            startActivity(new Intent(
                    this, CollectionActivity.class
            ));
        }).setBackgroundTint(
                getColor(R.color.primary)
        ).setTextColor(
                getColor(R.color.white)
        ).setActionTextColor(
                getColor(R.color.white)
        ).show();

        // Désactiver le bouton
        buttonAdd.setText("Ajouté ✓");
        buttonAdd.setEnabled(false);
        buttonAdd.setAlpha(0.7f);
    }

    // Formatter la date TMDB (YYYY-MM-DD → DD/MM/YYYY)
    private String formatDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) {
            return "Date inconnue";
        }
        try {
            String[] parts = rawDate.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1]
                        + "/" + parts[0];
            }
        } catch (Exception e) {
            // ignore
        }
        return rawDate;
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putString("selected_date", selectedDate);
        out.putFloat("personal_rating",
                ratingBarPersonal.getRating());
        out.putString("personal_note",
                editTextNote.getText().toString());
        out.putInt("spinner_pos",
                spinnerStatus.getSelectedItemPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle saved) {
        super.onRestoreInstanceState(saved);
        selectedDate = saved.getString(
                "selected_date", "");
        ratingBarPersonal.setRating(
                saved.getFloat("personal_rating", 0));
        editTextNote.setText(
                saved.getString("personal_note", ""));
        spinnerStatus.setSelection(
                saved.getInt("spinner_pos", 0));
        if (!selectedDate.isEmpty()) {
            textSelectedDate.setText(selectedDate);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
        return true;
    }
}