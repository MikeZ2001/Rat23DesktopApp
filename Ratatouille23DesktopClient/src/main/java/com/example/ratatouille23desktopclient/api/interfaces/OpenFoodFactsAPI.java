package com.example.ratatouille23desktopclient.api.interfaces;

import com.example.ratatouille23desktopclient.model.OFFAPIResponse;
import com.example.ratatouille23desktopclient.model.OFFAPISuggestion;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenFoodFactsAPI {

    @GET("cgi/search.pl?&action=process&json=1&fields=product_name&page_size=50")
    Call<OFFAPIResponse> getProductByName(@Query("search_terms") String name, @Query("page") int page);

    @GET("api/v3/taxonomy_suggestions?lc=it&limit=25&tagtype=allergens")
    Call<OFFAPISuggestion> getAllergens();
}
