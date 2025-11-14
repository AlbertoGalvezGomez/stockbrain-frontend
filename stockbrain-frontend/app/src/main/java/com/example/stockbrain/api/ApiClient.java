package com.example.stockbrain.api;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    //private static final String BASE_URL = "http://192.168.1.133:8080/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        if (context == null) {
                            return chain.proceed(chain.request());
                        }

                        SharedPreferences prefs = context.getSharedPreferences("data_login", Context.MODE_PRIVATE);
                        String userId = prefs.getString("user_id", "");
                        String email = prefs.getString("user_email", "");

                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("X-User-ID", userId)
                                .header("X-User-Email", email);

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
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