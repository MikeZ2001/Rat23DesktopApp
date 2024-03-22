package com.example.ratatouille23desktopclient.viewmodel;

import com.example.ratatouille23desktopclient.api.CheckedAPICallback;
import com.example.ratatouille23desktopclient.api.OpenFoodFactsService;
import com.example.ratatouille23desktopclient.api.RetrofitService;
import com.example.ratatouille23desktopclient.api.interfaces.OpenFoodFactsAPI;
import com.example.ratatouille23desktopclient.api.interfaces.ProductAPI;
import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.db.DBManager;
import com.example.ratatouille23desktopclient.db.ProdottoDAOPSQL;
import com.example.ratatouille23desktopclient.model.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductVM {
    private SimpleListProperty<Product> products = new SimpleListProperty<>(FXCollections.observableArrayList());
    private SimpleListProperty<Product> filteredProducts = new SimpleListProperty<>(FXCollections.observableArrayList());
    private SimpleListProperty<String> allergens = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final static Logger LOGGER = Logger.getLogger(ProductVM.class.getName());

    public SimpleListProperty<Product> productsProperty(){
        return this.products;
    }

    public SimpleListProperty<Product> filteredProductsProperty(){return this.filteredProducts;}

    public SimpleListProperty<String> allergensProperty(){
        return this.allergens;
    }

    public void getAllergens() throws IOException {
        OpenFoodFactsAPI api = OpenFoodFactsService.getRetrofit().create(OpenFoodFactsAPI.class);
        List<String> suggestions = api.getAllergens().execute().body().getSuggestions();
        suggestions.remove("Nessuno");
        allergens.get().setAll(suggestions);
    }

    public void removeAllergen(String allergen){
        Platform.runLater(() -> allergens.get().remove(allergen));
    }

    public void getSuggestions(String term, Runnable onUpdate){
        OpenFoodFactsAPI api = OpenFoodFactsService.getRetrofit().create(OpenFoodFactsAPI.class);
        api.getProductByName(term, 1).enqueue(new CheckedAPICallback<OFFAPIResponse>() {
            @Override
            public void onResponse(Call<OFFAPIResponse> call, Response<OFFAPIResponse> response) {
                LOGGER.log(Level.INFO, "Ricevuta response da OpenFoodFacts per i suggerimenti.");
                super.onResponse(call, response);
                if (isSuccessfull()){
                    Platform.runLater(() -> {
                        LOGGER.log(Level.INFO, "Response da OpenFoodFacts per i suggerimenti 200 OK.");
                        try {
                            LOGGER.log(Level.INFO, "Body response di OpenFoodFacts per i suggerimenti: {0}", response.body().getProducts());
                            Connection connection = DBManager.getInstance().getConnection();
                            ProdottoDAOPSQL daopsql = new ProdottoDAOPSQL(connection);
                            for (OFFProduct product: response.body().getProducts())
                                if (product.getName() != null)
                                    daopsql.insertProduct(product.getName());
                            if (response.body().getProducts().size() != 0)
                                onUpdate.run();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
    }

    public void getProducts(){
        long storeId = Long.valueOf(RAT23Cache.getCacheInstance().get("storeId").toString());
        ProductAPI api = RetrofitService.getRetrofit().create(ProductAPI.class);
        api.getAllProducts(storeId).enqueue(new CheckedAPICallback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                super.onResponse(call, response);
                if (isSuccessfull())
                    Platform.runLater(() -> products.get().setAll(response.body()));
            }
        });
    }

    public void updateProduct(Product product, Runnable onUpdateSuccess, Runnable onSaveSuccess){
        ProductAPI api = RetrofitService.getRetrofit().create(ProductAPI.class);

        if (products.get().contains(product)){
            api.updateProduct(product.getId(), product).enqueue(new CheckedAPICallback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    super.onResponse(call, response);
                    if (isSuccessfull())
                        Platform.runLater(() -> {
                            products.get().set(products.indexOf(product), product);
                            onUpdateSuccess.run();
                        });
                }
            });
        }else{
            api.createProduct(product).enqueue(new CheckedAPICallback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    super.onResponse(call, response);
                    if (isSuccessfull())
                        Platform.runLater(() -> {
                            products.get().add(response.body());
                            onSaveSuccess.run();
                        });
                }
            });
        }
    }

    public void deleteProduct(Product product){
        ProductAPI api = RetrofitService.getRetrofit().create(ProductAPI.class);

        api.deleteProduct(product.getId()).enqueue(new CheckedAPICallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                super.onResponse(call, response);
                if (isSuccessfull())
                    Platform.runLater(() -> products.get().remove(product));
            }
        });
    }

    public void filterProducts(String term){
        filteredProducts.set(FXCollections
                .observableArrayList(products.get().stream()
                        .filter(product -> product.getName().contains(term))
                        .collect(Collectors.toList()))
        );
    }
}
