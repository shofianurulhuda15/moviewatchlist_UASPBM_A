package com.moviewatchlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.moviewatchlist.R;
import com.moviewatchlist.model.Movie;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> movieList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Movie movie);
        void onDeleteClick(Movie movie);
        void onItemClick(Movie movie);
    }

    public MovieAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.movieList = new ArrayList<>();
        this.listener = listener;
    }

    public void setMovieList(List<Movie> movies) {
        this.movieList = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.titleTextView.setText(movie.getTitle());
        holder.genreTextView.setText(movie.getGenre());
        holder.statusTextView.setText(movie.getStatus().equals("watched") ? "Watched" : "Want to Watch");
        holder.ratingTextView.setText(String.format("%.1f", movie.getRating()));

        // Load placeholder image if no poster URL
        Glide.with(context)
                .load(movie.getPosterUrl() != null ? movie.getPosterUrl() : R.drawable.ic_movie_placeholder)
                .into(holder.posterImageView);

        holder.editButton.setOnClickListener(v -> listener.onEditClick(movie));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(movie));
        holder.cardView.setOnClickListener(v -> listener.onItemClick(movie));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, genreTextView, statusTextView, ratingTextView;
        ImageView posterImageView;
        MaterialButton editButton, deleteButton; // Ubah dari ImageView ke MaterialButton
        CardView cardView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_movie_title);
            genreTextView = itemView.findViewById(R.id.text_movie_genre);
            statusTextView = itemView.findViewById(R.id.text_movie_status);
            ratingTextView = itemView.findViewById(R.id.text_movie_rating);
            posterImageView = itemView.findViewById(R.id.image_movie_poster);
            editButton = itemView.findViewById(R.id.button_edit); // Ubah tipe ke MaterialButton
            deleteButton = itemView.findViewById(R.id.button_delete); // Ubah tipe ke MaterialButton
            cardView = itemView.findViewById(R.id.card_movie);
        }
    }
}