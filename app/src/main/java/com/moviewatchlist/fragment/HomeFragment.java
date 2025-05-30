package com.moviewatchlist.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.moviewatchlist.R;
import com.moviewatchlist.activity.LoginActivity;
import com.moviewatchlist.activity.MovieDetailActivity;
import com.moviewatchlist.adapter.MovieAdapter;
import com.moviewatchlist.api.ApiService;
import com.moviewatchlist.api.SupabaseClient;
import com.moviewatchlist.model.Movie;
import com.moviewatchlist.utils.Constants;
import com.moviewatchlist.utils.SharedPrefManager;

import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements MovieAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;
    private static final String TAG = "HomeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_movies);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        movieAdapter = new MovieAdapter(getContext(), this);
        recyclerView.setAdapter(movieAdapter);

        loadMovies();

        return view;
    }

    private void loadMovies() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = SupabaseClient.getClient().create(ApiService.class);
        String token = "Bearer " + SharedPrefManager.getInstance(getContext()).getUserToken();
        Log.d(TAG, "Token: " + token);
        Call<List<Movie>> call = apiService.getMovies(token, Constants.SUPABASE_ANON_KEY, "*");

        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Movies loaded: " + response.body().size());
                    movieAdapter.setMovieList(response.body());
                } else {
                    Log.e(TAG, "Failed to load movies, code: " + response.code());
                    Toast.makeText(getContext(), "Failed to load movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Network error: ", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(Movie movie) {
        Log.d(TAG, "Edit clicked for movie: " + movie.getTitle());
        EditMovieFragment fragment = EditMovieFragment.newInstance(movie);
        fragment.show(getParentFragmentManager(), "EditMovieFragment");
    }

    @Override
    public void onDeleteClick(Movie movie) {
        Log.d(TAG, "Delete clicked for movie: " + movie.getTitle());
        ApiService apiService = SupabaseClient.getClient().create(ApiService.class);
        String token = "Bearer " + SharedPrefManager.getInstance(getContext()).getUserToken();
        Call<Void> call = apiService.deleteMovie(token, Constants.SUPABASE_ANON_KEY, "eq." + movie.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Movie deleted", Toast.LENGTH_SHORT).show();
                    loadMovies();
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
                            errorMessage = "Failed to delete movie. Code: " + response.code();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: ", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Movie movie) {
        Log.d(TAG, "Item clicked for movie: " + movie.getTitle());
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }
}