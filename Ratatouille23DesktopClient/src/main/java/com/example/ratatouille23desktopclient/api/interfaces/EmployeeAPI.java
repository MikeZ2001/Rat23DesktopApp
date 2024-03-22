package com.example.ratatouille23desktopclient.api.interfaces;

import com.example.ratatouille23desktopclient.model.Employee;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface EmployeeAPI {
    String BASE_ENDPOINT = "api/v1/employee";

    @POST(BASE_ENDPOINT + "/add")
    Call<Employee> addEmployee(@Body Employee employee);

    @DELETE(BASE_ENDPOINT + "/delete/{employeeId}")
    Call<Void> deleteEmployee(@Path("employeeId") Long id);

    @PUT(BASE_ENDPOINT + "/update/{employeeId}")
    Call<Void> updateEmployee(@Path("employeeId") Long id, @Body Employee employee);

    @POST(BASE_ENDPOINT + "/getByEmail")
    Call<Employee> getEmployeeByEmail(@Body String email);

    @GET(BASE_ENDPOINT + "/getByStore/{id}")
    Call<List<Employee>> getEmployeesOfStore(@Path("id") Long storeId);
}
