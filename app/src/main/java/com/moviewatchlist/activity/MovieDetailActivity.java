package com.moviewatchlist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.moviewatchlist.R;
import com.moviewatchlist.api.ApiService;
import com.moviewatchlist.api.SupabaseClient;
import com.moviewatchlist.fragment.EditMovieFragment;
import com.moviewatchlist.model.Movie;
import com.moviewatchlist.utils.Constants;
import com.moviewatchlist.utils.SharedPrefManager;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "extra_movie";
    private static final String TAG = "MovieDetailActivity";
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            Log.e(TAG, "Toolbar not found in layout");
            finish();
            return;
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Movie Details");
        }

        // Inisialisasi view
        TextView titleTextView = findViewById(R.id.text_movie_title);
        TextView genreTextView = findViewById(R.id.text_movie_genre);
        TextView statusTextView = findViewById(R.id.text_movie_status);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        TextView descriptionTextView = findViewById(R.id.text_movie_description);
        ImageView posterImageView = findViewById(R.id.image_movie_poster);
        MaterialButton buttonEdit = findViewById(R.id.button_edit);
        MaterialButton buttonDelete = findViewById(R.id.button_delete);
        MaterialButton buttonClose = findViewById(R.id.button_close);

        // Ambil data film dari Intent
        movie = (Movie) getIntent().getSerializableExtra(EXTRA_MOVIE);
        if (movie == null) {
            Log.e(TAG, "Movie object is null");
            Toast.makeText(this, "Error: Movie data not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d(TAG, "Movie Data: Title=" + movie.getTitle() + ", Genre=" + movie.getGenre() + ", Description=" + movie.getDescription());

        // Atur data ke view
        titleTextView.setText(movie.getTitle() != null ? movie.getTitle() : "No title");
        genreTextView.setText(movie.getGenre() != null ? movie.getGenre() : "No genre");
        statusTextView.setText(movie.getStatus() != null && movie.getStatus().equals("watched") ? "Watched" : "Want to Watch");
        ratingBar.setRating(movie.getRating());
        descriptionTextView.setText(movie.getDescription() != null && !movie.getDescription().isEmpty() ? movie.getDescription() : "No description available");
        Glide.with(this)
                .load(movie.getPosterUrl() != null ? movie.getPosterUrl() : R.drawable.ic_movie_placeholder)
                .error(R.drawable.ic_movie_placeholder)
                .into(posterImageView);

        // Tombol Edit
        buttonEdit.setOnClickListener(v -> {
            Log.d(TAG, "Edit clicked for movie: " + movie.getTitle());
            EditMovieFragment fragment = EditMovieFragment.newInstance(movie);
            fragment.show(getSupportFragmentManager(), "EditMovieFragment");
        });

        // Tombol Delete
        buttonDelete.setOnClickListener(v -> {
            Log.d(TAG, "Delete clicked for movie: " + movie.getTitle());
            deleteMovie();
        });

        // Tombol Close
        buttonClose.setOnClickListener(v -> finish());
    }

    private void deleteMovie() {
        ApiService apiService = SupabaseClient.getClient().create(ApiService.class);
        String token = "Bearer " + SharedPrefManager.getInstance(this).getUserToken();
        Call<Void> call = apiService.deleteMovie(token, Constants.SUPABASE_ANON_KEY, "eq." + movie.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MovieDetailActivity.this, "Movie deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MovieDetailActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    String errorBody = "No error details";
                    if (response.errorBody() != null) {
                        try {
                            errorBody = response.errorBody().string();
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                        }
                    }
                    Log.e(TAG, "Error response: " + errorBody);
                    String errorMessage = "Failed to delete movie";
                    switch (response.code()) {
                        case 401:
                            errorMessage = "Authentication error: Please log in again";
                            startActivity(new Intent(MovieDetailActivity.this, LoginActivity.class));
                            finish();
                            break;
                        case 400:
                            errorMessage = "Invalid data provided. Details: " + errorBody;
                            break;
                        case 403:
                            errorMessage = "Permission denied";
                            break;
                        case 429:
                            errorMessage = "Too many requests, please try again later";
                            break;
                    }
                    Toast.makeText(MovieDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: ", t);
                Toast.makeText(MovieDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}