package com.example.stockbrain.api;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        if (context == null) {
                            return chain.proceed(chain.request());
                        }

                        SharedPreferences prefs = context.getSharedPreferences("data_login", Context.MODE_PRIVATE);

                        long userId = prefs.getLong("user_id", 0L);
                        String userIdStr = userId != 0L ? String.valueOf(userId) : "";

                        String email = prefs.getString("user_email", "");

                        Request.Builder requestBuilder = chain.request().newBuilder()
                                .header("X-User-ID", userIdStr)
                                .header("X-User-Email", email);

                        return chain.proceed(requestBuilder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}