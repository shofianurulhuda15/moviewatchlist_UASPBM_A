package com.moviewatchlist.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.moviewatchlist.R;
import com.moviewatchlist.activity.LoginActivity;
import com.moviewatchlist.utils.SharedPrefManager;

public class ProfileFragment extends Fragment {
    private TextView emailTextView, nameTextView;
    private ImageView avatarImageView;
    private MaterialButton logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inisialisasi view
        emailTextView = view.findViewById(R.id.text_email);
        nameTextView = view.findViewById(R.id.text_name);
        avatarImageView = view.findViewById(R.id.avatar_image);
        logoutButton = view.findViewById(R.id.button_logout);

        // Set data pengguna dari SharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());
        String email = sharedPrefManager.getUserEmail();
        emailTextView.setText(email != null ? email : "No email available");

        // Set nama pengguna (default jika tidak tersedia)
        String userName = sharedPrefManager.getUserName(); // Perlu ditambahkan di SharedPrefManager
        nameTextView.setText(userName != null ? userName : "User");

        // Set avatar (opsional: muat dari URL atau gunakan placeholder)
        // Jika ada URL avatar di SharedPrefManager, gunakan Glide/Picasso
        avatarImageView.setImageResource(R.drawable.ic_profile);

        // Atur listener untuk tombol logout
        logoutButton.setOnClickListener(v -> {
            sharedPrefManager.logout();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }
}