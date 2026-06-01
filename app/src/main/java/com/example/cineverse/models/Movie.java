package com.example.cineverse.models;

public class Movie {

    // Données TMDB
    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private double rating;
    private String releaseDate;

    // Données personnelles
    private String status; // "À voir", "En cours", "Vu"
    private float personalRating;
    private String personalNote;
    private String watchDate;

    // Constructeur TMDB (recherche)
    public Movie(int id,
                 String title,
                 String overview,
                 String posterPath,
                 double rating,
                 String releaseDate) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.status = "À voir";
        this.personalRating = 0;
        this.personalNote = "";
        this.watchDate = "";
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public double getRating() { return rating; }
    public String getReleaseDate() { return releaseDate; }
    public String getStatus() { return status; }
    public float getPersonalRating() { return personalRating; }
    public String getPersonalNote() { return personalNote; }
    public String getWatchDate() { return watchDate; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setPersonalRating(float personalRating) { this.personalRating = personalRating; }
    public void setPersonalNote(String personalNote) { this.personalNote = personalNote; }
    public void setWatchDate(String watchDate) { this.watchDate = watchDate; }
}
