package com.example.ratatouille23desktopclient.viewmodel;

import com.example.ratatouille23desktopclient.api.CheckedAPICallback;
import com.example.ratatouille23desktopclient.api.RetrofitService;
import com.example.ratatouille23desktopclient.api.interfaces.CategoryAPI;
import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.model.Category;
import com.example.ratatouille23desktopclient.model.Store;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import retrofit2.Call;
import retrofit2.Response;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryVM {
    private SimpleListProperty<Category> categories = new SimpleListProperty<>(FXCollections.observableArrayList());
    private SimpleListProperty<Category> filteredCategories = new SimpleListProperty<>(FXCollections.observableArrayList());

    public SimpleListProperty<Category> categoriesProperty(){
        return this.categories;
    }
    public SimpleListProperty<Category> filteredCategoriesProperty(){
        return this.filteredCategories;
    }

    public void getCategories(){
        CategoryAPI categoryAPI = RetrofitService.getRetrofit().create(CategoryAPI.class);
        long storeId = Long.valueOf(RAT23Cache.getCacheInstance().get("storeId").toString());
        categoryAPI.getAllCategories(storeId).enqueue(new CheckedAPICallback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                super.onResponse(call, response);
                if (isSuccessfull())
                    Platform.runLater(() -> {
                        categories.get().setAll(response.body());
                    });
            }
        });
    }

    public void updateCategories(Category category, Runnable onSaveSuccess, Runnable onUpdateSuccess) {
        CategoryAPI api = RetrofitService.getRetrofit().create(CategoryAPI.class);
        if (categories.get().contains(category)) {
            api.updateCategory(category.getId(), category).enqueue(new CheckedAPICallback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    super.onResponse(call, response);
                    if (isSuccessfull())
                        Platform.runLater(() -> {
                            categories.get().set(categories.get().indexOf(category), category);
                            onUpdateSuccess.run();
                        });
                }
            });
        } else {
            Store currentStore = new Store();
            currentStore.setId(Long.valueOf(RAT23Cache.getCacheInstance().get("storeId").toString()));
            category.setStore(currentStore);
            api.createCategory(category).enqueue(new CheckedAPICallback<Category>() {
                @Override
                public void onResponse(Call<Category> call, Response<Category> response) {
                    super.onResponse(call, response);
                    if (isSuccessfull())
                        Platform.runLater(() -> {
                            categories.get().add(response.body());
                            onSaveSuccess.run();
                        });
                }
            });
        }
    }

    public void deleteCategory(Category category){
        CategoryAPI api = RetrofitService.getRetrofit().create(CategoryAPI.class);

        api.deleteCategory(category.getId()).enqueue(new CheckedAPICallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                super.onResponse(call, response);
                if (isSuccessfull())
                    Platform.runLater(() -> categories.get().remove(category));
            }
        });
    }

    public void filterCategories(String term){
        filteredCategories.set(FXCollections
                .observableArrayList(categories.get().stream()
                        .filter(categoria -> categoria.getName().contains(term))
                        .collect(Collectors.toList()))
        );
    }
}
