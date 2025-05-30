package com.moviewatchlist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.moviewatchlist.R;
import com.moviewatchlist.utils.Constants;
import com.moviewatchlist.utils.SharedPrefManager;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.edit_email);
        passwordEditText = findViewById(R.id.edit_password);
        loginButton = findViewById(R.id.button_login);
        registerTextView = findViewById(R.id.text_register);

        loginButton.setOnClickListener(v -> loginUser());
        registerTextView.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (Exception e) {
            Log.e("LoginActivity", "JSON error: ", e);
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(Constants.SUPABASE_URL + "/auth/v1/token?grant_type=password")
                .post(body)
                .addHeader("apikey", Constants.SUPABASE_ANON_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String token = jsonResponse.getString("access_token");

                        // Decode JWT untuk mendapatkan UUID (sub)
                        String[] tokenParts = token.split("\\.");
                        String payload = new String(Base64.decode(tokenParts[1], Base64.URL_SAFE));
                        JSONObject payloadJson = new JSONObject(payload);
                        String userId = payloadJson.getString("sub"); // UUID pengguna

                        // Gunakan email sebagai nama pengguna sementara
                        String userName = email; // Alternatif: null atau ambil dari tabel profiles

                        Log.d("LoginActivity", "Token: " + token);
                        Log.d("LoginActivity", "User ID: " + userId);
                        Log.d("LoginActivity", "User Name: " + userName);

                        // Simpan data pengguna
                        SharedPrefManager.getInstance(LoginActivity.this)
                                .saveUser(userId, email, token, userName);

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No error details";
                    Log.e("LoginActivity", "Login failed: " + errorBody);
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid credentials: " + errorBody, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}