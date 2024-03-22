package com.example.ratatouille23desktopclient.api;

import com.google.gson.Gson;
import org.hildan.fxgson.FxGson;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static Retrofit retrofit = null;

    private static final String PORT = "8080";
    private static final String BASE_URL = "http://ec2-35-158-30-11.eu-central-1.compute.amazonaws.com:"+PORT+"/";
    //private static final String BASE_URL = "http://192.168.1.165:"+PORT+"/";
    private RetrofitService(){

    }

    public static Retrofit getRetrofit(){
        if (retrofit == null){
            Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
