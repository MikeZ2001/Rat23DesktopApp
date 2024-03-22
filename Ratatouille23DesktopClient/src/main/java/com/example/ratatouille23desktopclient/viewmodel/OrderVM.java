package com.example.ratatouille23desktopclient.viewmodel;

import com.example.ratatouille23desktopclient.api.CheckedAPICallback;
import com.example.ratatouille23desktopclient.api.RetrofitService;
import com.example.ratatouille23desktopclient.api.interfaces.OrderAPI;
import com.example.ratatouille23desktopclient.api.interfaces.TableAPI;
import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.model.Order;
import com.example.ratatouille23desktopclient.model.OrderItem;
import com.example.ratatouille23desktopclient.model.Table;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import retrofit2.Call;
import retrofit2.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OrderVM {

    SimpleObjectProperty<Order> currentOrder = new SimpleObjectProperty<>();

    public SimpleObjectProperty<Order> currentOrderProperty() {
        return currentOrder;
    }

    public void getTableOrder(Table table, Runnable onSuccess){
        OrderAPI api = RetrofitService.getRetrofit().create(OrderAPI.class);
        api.getCurrentOrderOfTable(table.getId()).enqueue(new CheckedAPICallback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                super.onResponse(call, response);
                if (isSuccessfull()){
                    if (response.body() != null){
                        Platform.runLater(() -> {
                            currentOrder.set(response.body());
                            onSuccess.run();
                        });
                    }
                }
            }
        });
    }

    public void payOrder(Table table, Runnable paymentSuccess){
        currentOrder.get().setStatus(Order.Status.PAYED);

        List<Employee> employees;
        if (currentOrder.get().getEmployeesOfTheOrder() != null)
            employees = currentOrder.get().getEmployeesOfTheOrder();
        else
            employees = new ArrayList<>();
        Employee currentEmployee = new Employee();
        currentEmployee.setId(Long.valueOf(RAT23Cache.getCacheInstance().get("currentUserId").toString()));
        employees.add(currentEmployee);
        currentOrder.get().setEmployeesOfTheOrder(FXCollections.observableArrayList(employees));

        for (OrderItem item: currentOrder.get().getItems()){
            if (item.getOrderItemStatus().equals(OrderItem.OrderItemStatus.NOT_READY))
                item.setOrderItemStatus(OrderItem.OrderItemStatus.READY);
        }

        OrderAPI orderAPI = RetrofitService.getRetrofit().create(OrderAPI.class);
        TableAPI tableAPI = RetrofitService.getRetrofit().create(TableAPI.class);
        orderAPI.updateOrderById(currentOrder.get().getId(), currentOrder.get()).enqueue(new CheckedAPICallback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                super.onResponse(call, response);
                if (isSuccessfull()){
                    table.setAvailable(true);
                    tableAPI.updateTable(table.getId(), table).enqueue(new CheckedAPICallback<Table>() {
                        @Override
                        public void onResponse(Call<Table> call, Response<Table> response) {
                            super.onResponse(call, response);
                            if(isSuccessfull()){
                                Platform.runLater(() -> paymentSuccess.run());
                            }
                        }
                    });
                }
            }
        });
    }

    public void countOrdersOfEmployeeByDate(Employee employee, LocalDate day, Function<Integer, Boolean> onSuccess){
        long storeId = Long.valueOf(RAT23Cache.getCacheInstance().get("storeId").toString());
        OrderAPI api = RetrofitService.getRetrofit().create(OrderAPI.class);
        api.countOrdersOfEmployeeInDate(storeId, employee.getId(), day).enqueue(new CheckedAPICallback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                super.onResponse(call, response);
                if (isSuccessfull()){
                    Platform.runLater(() -> onSuccess.apply(response.body()));
                }
            }
        });
    }
}
