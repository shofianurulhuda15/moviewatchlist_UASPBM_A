package com.moviewatchlist.api;

import com.moviewatchlist.model.Movie;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("rest/v1/movies")
    @Headers("Content-Type: application/json")
    Call<Void> addMovie(@Header("Authorization") String authToken, @Header("apikey") String apiKey, @Body Movie movie);

    @GET("rest/v1/movies")
    Call<List<Movie>> getMovies(@Header("Authorization") String authToken, @Header("apikey") String apiKey, @Query("select") String select);

    @PUT("rest/v1/movies")
    Call<Void> updateMovie(@Header("Authorization") String authToken, @Header("apikey") String apiKey, @Query("id") String id, @Body Movie movie);

    @DELETE("rest/v1/movies")
    Call<Void> deleteMovie(@Header("Authorization") String authToken, @Header("apikey") String apiKey, @Query("id") String id);
}