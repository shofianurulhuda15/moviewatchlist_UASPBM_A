package com.moviewatchlist.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import com.moviewatchlist.R;
import com.moviewatchlist.activity.LoginActivity;
import com.moviewatchlist.api.ApiService;
import com.moviewatchlist.api.SupabaseClient;
import com.moviewatchlist.model.Movie;
import com.moviewatchlist.utils.Constants;
import com.moviewatchlist.utils.SharedPrefManager;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;

public class EditMovieFragment extends DialogFragment {
    private EditText titleEditText, genreEditText, descriptionEditText, posterUrlEditText;
    private Spinner statusSpinner;
    private RatingBar ratingBar;
    private Button saveButton;
    private Movie movie;

    public static EditMovieFragment newInstance(Movie movie) {
        EditMovieFragment fragment = new EditMovieFragment();
        Bundle args = new Bundle();
        args.putSerializable("movie", movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_movie, container, false);

        titleEditText = view.findViewById(R.id.edit_movie_title);
        genreEditText = view.findViewById(R.id.edit_movie_genre);
        descriptionEditText = view.findViewById(R.id.edit_movie_description);
        posterUrlEditText = view.findViewById(R.id.edit_movie_poster_url);
        statusSpinner = view.findViewById(R.id.spinner_status);
        ratingBar = view.findViewById(R.id.rating_bar);
        saveButton = view.findViewById(R.id.button_save);

        movie = (Movie) getArguments().getSerializable("movie");

        titleEditText.setText(movie.getTitle());
        genreEditText.setText(movie.getGenre());
        descriptionEditText.setText(movie.getDescription());
        posterUrlEditText.setText(movie.getPosterUrl());
        ratingBar.setRating(movie.getRating());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        statusSpinner.setSelection(movie.getStatus().equals("watched") ? 0 : 1);

        saveButton.setOnClickListener(v -> updateMovie());

        return view;
    }

    private void updateMovie() {
        String title = titleEditText.getText().toString().trim();
        String genre = genreEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String posterUrl = posterUrlEditText.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString().equals("Watched") ? "watched" : "want_to_watch";
        float rating = ratingBar.getRating();

        if (title.isEmpty() || genre.isEmpty()) {
            Toast.makeText(getContext(), "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = SharedPrefManager.getInstance(getContext()).getUserToken();
        String userId = SharedPrefManager.getInstance(getContext()).getUserId();
        if (token == null || token.isEmpty() || userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "Authentication error: Please log in again", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
            return;
        }

        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setDescription(description);
        movie.setPosterUrl(posterUrl.isEmpty() ? null : posterUrl);
        movie.setStatus(status);
        movie.setRating(rating);
        movie.setUserId(userId);

        Log.d("EditMovieFragment", "Token: " + token);
        Log.d("EditMovieFragment", "User ID: " + userId);
        Log.d("EditMovieFragment", "Movie Data: " + new Gson().toJson(movie));

        ApiService apiService = SupabaseClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.updateMovie("Bearer " + token, Constants.SUPABASE_ANON_KEY, "eq." + movie.getId(), movie);

        Log.d("EditMovieFragment", "Request URL: " + call.request().url());
        Log.d("EditMovieFragment", "Request Headers: " + call.request().headers());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("EditMovieFragment", "Response Code: " + response.code());
                Log.d("EditMovieFragment", "Response Headers: " + response.headers());
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Movie updated", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    String errorBody = "No error details";
                    if (response.errorBody() != null) {
                        try {
                            errorBody = response.errorBody().string();
                        } catch (IOException e) {
                            Log.e("EditMovieFragment", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Log.e("EditMovieFragment", "Error response: " + errorBody);
                    String errorMessage = "Failed to update movie";
                    switch (response.code()) {
                        case 401:
                            errorMessage = "Authentication error: Please log in again";
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            requireActivity().finish();
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
                        default:
                            errorMessage = "Failed to update movie. Code: " + response.code();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("EditMovieFragment", "Network error: ", t);
            }
        });
    }
}