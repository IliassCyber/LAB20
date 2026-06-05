package com.example.numberbook;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Note : 10.0.2.2 est l'adresse spéciale pour accéder au localhost de votre ordinateur
    // depuis l'émulateur Android. Si vous utilisez un appareil physique, utilisez l'IP de votre PC.
    private static final String SERVER_URL = "http://10.0.2.2:8080/numberbook-api/api/";
    private static Retrofit instance;

    public static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}
