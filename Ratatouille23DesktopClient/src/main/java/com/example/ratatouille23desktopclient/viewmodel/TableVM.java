package com.example.ratatouille23desktopclient.viewmodel;

import com.example.ratatouille23desktopclient.api.CheckedAPICallback;
import com.example.ratatouille23desktopclient.api.RetrofitService;
import com.example.ratatouille23desktopclient.api.interfaces.TableAPI;
import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.model.Store;
import com.example.ratatouille23desktopclient.model.Table;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.stream.Collectors;

public class TableVM {
    private SimpleListProperty<Table> tables = new SimpleListProperty<>(FXCollections.observableArrayList());
    private SimpleListProperty<Table> filteredTables = new SimpleListProperty<>(FXCollections.observableArrayList());

    public SimpleListProperty<Table> tablesProperty(){
        return this.tables;
    }

    public SimpleListProperty<Table> filteredTablesProperty(){
        return this.filteredTables;
    }

    public void getTables(){
        TableAPI api = RetrofitService.getRetrofit().create(TableAPI.class);
        long storeId = Long.valueOf(RAT23Cache.getCacheInstance().get("storeId").toString());
        api.getAllTables(storeId).enqueue(new CheckedAPICallback<List<Table>>() {
            @Override
            public void onResponse(Call<List<Table>> call, Response<List<Table>> response) {
                super.onResponse(call, response);
                if (isSuccessfull())
                    Platform.runLater(() -> tables.get().setAll(response.body()));
            }
        });
    }

    public void updateTavoli(Table table, Runnable onUpdateSuccess, Runnable onSaveSuccess){
        TableAPI api = RetrofitService.getRetrofit().create(TableAPI.class);

        if (tables.get().contains(table)){
            api.updateTable(table.getId(), table).enqueue(new CheckedAPICallback<Table>() {
                    @Override
                    public void onResponse(Call<Table> call, Response<Table> response) {
                        super.onResponse(call, response);
                        if (isSuccessfull())
                            Platform.runLater(() -> {
                                tables.get().set(tables.get().indexOf(table), table);
                                onUpdateSuccess.run();
                            });
                    }
                });
        }else{
            Store currentStore = new Store();
            currentStore.setId(Long.valueOf(RAT23Cache.getCacheInstance().get("storeId").toString()));
            table.setStore(currentStore);
            api.createTable(table).enqueue(new CheckedAPICallback<Table>() {
                    @Override
                    public void onResponse(Call<Table> call, Response<Table> response) {
                        super.onResponse(call, response);
                        if (isSuccessfull())
                            Platform.runLater(() -> {
                                tables.get().add(response.body());
                                onSaveSuccess.run();
                            });
                    }
                });
        }
    }

    public void deleteTable(Table table){
        TableAPI api = RetrofitService.getRetrofit().create(TableAPI.class);
        api.deleteTable(table.getId()).enqueue(new CheckedAPICallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                super.onResponse(call, response);
                if (isSuccessfull()){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            tables.get().remove(table);
                        }
                    });
                }
            }
        });
    }

    public void filterTables(String term){
        filteredTables.set(FXCollections
                .observableArrayList(tables.get().stream()
                        .filter(table -> table.getName().contains(term))
                        .collect(Collectors.toList()))
        );
    }
}
