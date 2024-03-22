package com.example.ratatouille23desktopclient.api.interfaces;

import com.example.ratatouille23desktopclient.model.Order;
import com.example.ratatouille23desktopclient.model.Table;
import javafx.scene.control.Tab;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface TableAPI {
    String BASE_ENDPOINT = "api/v1/table";

    @PUT(BASE_ENDPOINT + "/update/{tableId}")
    Call<Table> updateTable(@Path("tableId") Long id, @Body Table updatedTable);

    @DELETE(BASE_ENDPOINT + "/delete/{tableId}")
    Call<Void> deleteTable(@Path("tableId") Long id);

    @POST(BASE_ENDPOINT + "/add")
    Call<Table> createTable(@Body Table table);

    @GET(BASE_ENDPOINT + "/getAllByStore/{storeId}")
    Call<List<Table>> getAllTables(@Path("storeId") Long id);
}
