package com.example.ratatouille23desktopclient.api.interfaces;

import com.example.ratatouille23desktopclient.model.Product;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ProductAPI {
    String BASE_ENDPOINT = "api/v1/product";

    @GET(BASE_ENDPOINT + "/getAllByStore/{storeId}")
    Call<List<Product>> getAllProducts(@Path("storeId") Long id);

    @PUT(BASE_ENDPOINT + "/update/{id}")
    Call<Product> updateProduct(@Path("id") Long id, @Body Product product);

    @DELETE(BASE_ENDPOINT + "/delete/{id}")
    Call<Void> deleteProduct(@Path("id") Long id);

    @POST(BASE_ENDPOINT + "/add")
    Call<Product> createProduct(@Body Product product);
}
