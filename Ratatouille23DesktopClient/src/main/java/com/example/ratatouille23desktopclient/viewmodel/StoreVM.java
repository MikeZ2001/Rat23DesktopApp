package com.example.ratatouille23desktopclient.viewmodel;

import com.example.ratatouille23desktopclient.api.CheckedAPICallback;
import com.example.ratatouille23desktopclient.api.RetrofitService;
import com.example.ratatouille23desktopclient.api.interfaces.EmployeeAPI;
import com.example.ratatouille23desktopclient.api.interfaces.StoreAPI;
import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.model.Store;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import retrofit2.Call;
import retrofit2.Response;

public class StoreVM {
    private SimpleLongProperty storeId = new SimpleLongProperty();
    private SimpleStringProperty storeName = new SimpleStringProperty(),
            storeEmail = new SimpleStringProperty(),
            storeAddress = new SimpleStringProperty(),
            storePhone = new SimpleStringProperty();

    public SimpleStringProperty storeNameProperty() {
        return storeName;
    }
    public SimpleStringProperty storeEmailProperty() {
        return storeEmail;
    }
    public SimpleStringProperty storeAddressProperty() {
        return storeAddress;
    }
    public SimpleStringProperty storePhoneProperty() {
        return storePhone;
    }

    public void getCurrentStore(){
        RAT23Cache<String, String> cache = RAT23Cache.getCacheInstance();
        String currentUserEmail = cache.get("currentUserEmail");
        EmployeeAPI api = RetrofitService.getRetrofit().create(EmployeeAPI.class);
        api.getEmployeeByEmail(currentUserEmail).enqueue(new CheckedAPICallback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                super.onResponse(call, response);
                if (isSuccessfull()){
                    Platform.runLater(() -> {
                        storeId.set(response.body().getStore().getId());
                        storeName.set(response.body().getStore().getName());
                        storeAddress.set(response.body().getStore().getAddress());
                        storePhone.set(response.body().getStore().getPhone());
                        storeEmail.set(response.body().getStore().getEmail());
                        cache.put("currentUserId", String.valueOf(response.body().getId()));
                        cache.put("storeId", String.valueOf(response.body().getStore().getId()));
                    });
                }
            }
        });
    }

    public void saveStore(Runnable onSuccess){
        Store store = new Store();
        store.setId(storeId.get());
        store.setName(storeName.get());
        store.setAddress(storeAddress.get());
        store.setEmail(storeEmail.get());
        store.setPhone(storePhone.get());
        StoreAPI storeAPI = RetrofitService.getRetrofit().create(StoreAPI.class);
        storeAPI.updateStore(storeId.get(), store).enqueue(new CheckedAPICallback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                super.onResponse(call, response);
                if (isSuccessfull())
                    Platform.runLater(() -> {
                        storeId.set(response.body().getId());
                        storeName.set(response.body().getName());
                        storeAddress.set(response.body().getAddress());
                        storePhone.set(response.body().getPhone());
                        storeEmail.set(response.body().getEmail());
                        onSuccess.run();
                    });
            }
        });
    }
}
