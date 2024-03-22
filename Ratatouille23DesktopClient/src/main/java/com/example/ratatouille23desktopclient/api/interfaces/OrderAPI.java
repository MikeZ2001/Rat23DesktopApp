package com.example.ratatouille23desktopclient.api.interfaces;

import com.example.ratatouille23desktopclient.model.Order;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface OrderAPI {
    String BASE_ENDPOINT = "api/v1/order";

    @PUT(BASE_ENDPOINT + "/update/{id}")
    Call<Order> updateOrderById(@Path("id")Long id, @Body Order order);

    @GET(BASE_ENDPOINT + "/{storeId}/getOrdersOfEmployee/{employeeId}/{date}")
    Call<Integer> countOrdersOfEmployeeInDate(@Path("storeId") Long storeId, @Path("employeeId") Long employeeId, @Path("date") LocalDate date);

    @GET(BASE_ENDPOINT + "/getCurrentOrderOfTable/{id}")
    Call<Order> getCurrentOrderOfTable(@Path("id") Long id);
}
