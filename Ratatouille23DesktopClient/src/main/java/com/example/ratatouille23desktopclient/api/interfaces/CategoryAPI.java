package com.example.ratatouille23desktopclient.api.interfaces;

import com.example.ratatouille23desktopclient.model.Category;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface CategoryAPI {
    String BASE_ENDPOINT = "api/v1/category";

    @GET(BASE_ENDPOINT + "/getByStore/{storeId}")
    Call<List<Category>> getAllCategories(@Path("storeId") Long id);

    @PUT(BASE_ENDPOINT + "/update/{id}")
    Call<Void> updateCategory(@Path("id") Long id, @Body Category category);

    @DELETE(BASE_ENDPOINT + "/delete/{id}")
    Call<Void> deleteCategory(@Path("id") Long id);

    @POST(BASE_ENDPOINT + "/add")
    Call<Category> createCategory(@Body Category category);
}
