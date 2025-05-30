package com.moviewatchlist.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Movie implements Serializable {
    @SerializedName("id")
    private String id; // UUID or integer, depending on Supabase table
    @SerializedName("title")
    private String title;
    @SerializedName("genre")
    private String genre;
    @SerializedName("description")
    private String description;
    @SerializedName("status")
    private String status;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("rating")
    private float rating;
    @SerializedName("poster_url") // Match Supabase column name
    private String posterUrl;

    public Movie(String title, String genre, String description, String status) {
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.status = status;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
}