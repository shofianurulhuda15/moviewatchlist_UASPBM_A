package com.moviewatchlist.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.gson.Gson;
import com.moviewatchlist.R;
import com.moviewatchlist.activity.LoginActivity;
import com.moviewatchlist.api.ApiService;
import com.moviewatchlist.api.SupabaseClient;
import com.moviewatchlist.model.Movie;
import com.moviewatchlist.utils.Constants;
import com.moviewatchlist.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;
import android.view.inputmethod.InputMethodManager;
import android.content.DialogInterface;

public class AddMovieFragment extends DialogFragment {
    private static final String TAG = "AddMovieFragment";
    private EditText titleEditText, genreEditText, descriptionEditText, posterUrlEditText;
    private Spinner statusSpinner;
    private Button saveButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_movie, container, false);
        Toast.makeText(requireContext(), "Layout Loaded", Toast.LENGTH_SHORT).show();
        initViews(view);
        setupSpinner();
        setupClickListeners(view);
        return view;
    }

    private void initViews(View view) {
        titleEditText = view.findViewById(R.id.edit_movie_title);
        genreEditText = view.findViewById(R.id.edit_movie_genre);
        descriptionEditText = view.findViewById(R.id.edit_movie_description);
        posterUrlEditText = view.findViewById(R.id.edit_movie_poster_url);
        statusSpinner = view.findViewById(R.id.spinner_status);
        saveButton = view.findViewById(R.id.button_save);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
    }

    private void setupClickListeners(View view) {
        saveButton.setOnClickListener(v -> saveMovie());
        view.findViewById(R.id.button_cancel).setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Atur lebar dialog menjadi 90% dari lebar layar
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);

            // Atur tinggi dialog agar menyesuaikan konten dengan batas maksimum
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            // Atur dimensi dialog
            getDialog().getWindow().setLayout(width, height);

            // Pastikan latar belakang transparan agar sesuai dengan dialog_background.xml
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Animasi dialog
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);

            // Pastikan dialog tidak terlalu kecil dengan menambahkan minimum dimensi
            getDialog().getWindow().setLayout(
                    Math.max(width, (int) (300 * getResources().getDisplayMetrics().density)), // Minimum 300dp
                    height
            );
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onDismiss(dialog);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }



    private void saveMovie() {
        Log.d(TAG, "Starting saveMovie");
        String title = titleEditText.getText().toString().trim();
        String genre = genreEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String posterUrl = posterUrlEditText.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString().equals("Watched") ? "watched" : "want_to_watch";

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

        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
            saveButton.setEnabled(true);
            saveButton.setText("Save");
            return;
        }

        saveButton.setEnabled(false);
        saveButton.setText("Saving...");

        Movie movie = new Movie(title, genre, description, status);
        movie.setUserId(userId);
        movie.setPosterUrl(posterUrl.isEmpty() ? null : posterUrl);
        movie.setRating(0.0f);

        Log.d(TAG, "Movie Data: " + new Gson().toJson(movie));
        Log.d(TAG, "Token: " + token); // Log token
        Log.d(TAG, "API Key: " + Constants.SUPABASE_ANON_KEY); // Log API key

        ApiService apiService = SupabaseClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.addMovie("Bearer " + token, Constants.SUPABASE_ANON_KEY, movie);
        Log.d(TAG, "Request URL: " + call.request().url());
        Log.d(TAG, "Request Headers: " + call.request().headers());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                saveButton.setEnabled(true);
                saveButton.setText("Save");
                Log.d(TAG, "Response Code: " + response.code()); // Log status code
                Log.d(TAG, "Response Headers: " + response.headers()); // Log headers
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Movie added successfully", Toast.LENGTH_SHORT).show();
                    View view = getView();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    dismiss();
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
                    String errorMessage = "Failed to add movie";
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
                            errorMessage = "Failed to add movie. Code: " + response.code();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                saveButton.setEnabled(true);
                saveButton.setText("Save");
                Log.e(TAG, "Network error: ", t);
                //Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}