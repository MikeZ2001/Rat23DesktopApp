package com.example.ratatouille23desktopclient.api;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import org.hildan.fxgson.FxGson;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.Duration;

public class OpenFoodFactsService {
    private static final String baseURL = "https://it.openfoodfacts.org/";

    private static Retrofit retrofit;

    private OpenFoodFactsService(){

    }


    public static Retrofit getRetrofit(){
        if (retrofit == null){
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .readTimeout(Duration.ofSeconds(30))
                    .writeTimeout(Duration.ofSeconds(15))
                    .build();

            Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
