package com.example.ratatouille23desktopclient.api.interfaces;

import com.example.ratatouille23desktopclient.model.Store;
import retrofit2.Call;
import retrofit2.http.*;

public interface StoreAPI {
    String BASE_ENDPOINT = "api/v1/store";

    @PUT(BASE_ENDPOINT + "/update/{id}")
    Call<Store> updateStore(@Path("id") long id, @Body Store store);
}
