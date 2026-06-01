package com.example.cineverse.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cineverse.R;
import com.example.cineverse.utils.FileManager;
import com.example.cineverse.utils.PreferenceManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText editTextUsername;
    private TextInputLayout   layoutUsername;
    private SwitchMaterial    switchNotif;
    private SwitchMaterial    switchAutoSave;
    private MaterialButton    buttonSave;
    private MaterialButton    buttonClearCollection;
    private PreferenceManager preferenceManager;
    private FileManager       fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Toolbar
        MaterialToolbar toolbar =
                findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()
                    .setTitle("Paramètres");
        }

        // Vues
        layoutUsername       = findViewById(R.id.layoutUsername);
        editTextUsername     = findViewById(R.id.editTextUsername);
        switchNotif          = findViewById(R.id.switchNotif);
        switchAutoSave       = findViewById(R.id.switchAutoSave);
        buttonSave           = findViewById(R.id.buttonSave);
        buttonClearCollection= findViewById(R.id.buttonClearCollection);

        preferenceManager = new PreferenceManager(this);
        fileManager       = new FileManager(this);

        // Charger données sauvegardées
        editTextUsername.setText(
                preferenceManager.getUsername()
        );
        switchNotif.setChecked(
                preferenceManager.getNotifications()
        );
        switchAutoSave.setChecked(
                preferenceManager.getAutoSave()
        );

        // Bouton Sauvegarder
        buttonSave.setOnClickListener(v -> {
            String username = editTextUsername
                    .getText().toString().trim();

            if (username.isEmpty()) {
                layoutUsername.setError(
                        "Entre ton nom d'utilisateur"
                );
                return;
            }

            layoutUsername.setError(null);
            preferenceManager.saveUsername(username);
            preferenceManager.saveNotifications(
                    switchNotif.isChecked()
            );
            preferenceManager.saveAutoSave(
                    switchAutoSave.isChecked()
            );

            Snackbar.make(
                    v,
                    "Paramètres sauvegardés !",
                    Snackbar.LENGTH_SHORT
            ).setBackgroundTint(
                    getColor(R.color.primary)
            ).setTextColor(
                    getColor(R.color.white)
            ).show();
        });

        // Bouton vider collection
        buttonClearCollection.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog
                    .Builder(this)
                    .setTitle("Vider la collection")
                    .setMessage(
                            "Es-tu sûr de vouloir "
                                    + "supprimer tous tes films ?"
                    )
                    .setPositiveButton(
                            "Vider",
                            (dialog, which) -> {
                                fileManager.saveCollection(
                                        new java.util.ArrayList<>()
                                );
                                Snackbar.make(
                                        v,
                                        "Collection vidée",
                                        Snackbar.LENGTH_SHORT
                                ).show();
                            })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Auto-save si activé
        if (switchAutoSave.isChecked()) {
            String username = editTextUsername
                    .getText().toString().trim();
            if (!username.isEmpty()) {
                preferenceManager.saveUsername(username);
                preferenceManager.saveNotifications(
                        switchNotif.isChecked()
                );
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putString("username",
                editTextUsername.getText().toString());
        out.putBoolean("notif",
                switchNotif.isChecked());
        out.putBoolean("autosave",
                switchAutoSave.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle saved) {
        super.onRestoreInstanceState(saved);
        editTextUsername.setText(
                saved.getString("username", ""));
        switchNotif.setChecked(
                saved.getBoolean("notif", false));
        switchAutoSave.setChecked(
                saved.getBoolean("autosave", false));
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