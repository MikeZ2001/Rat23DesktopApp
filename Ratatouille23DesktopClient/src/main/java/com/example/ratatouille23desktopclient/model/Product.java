package com.example.ratatouille23desktopclient.model;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.Objects;

public class Product {
    private SimpleLongProperty id;
    @SerializedName("name")
    private SimpleStringProperty name;
    @SerializedName("description")
    private SimpleStringProperty description;
    @SerializedName("price")
    private SimpleDoubleProperty price;

    @SerializedName("categoryOfTheProduct")
    private SimpleObjectProperty<Category> category;

    @SerializedName("allergens")
    private SimpleListProperty<String> allergens;

    public Product() {
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.category = new SimpleObjectProperty<>();
        this.description = new SimpleStringProperty();
        this.allergens = new SimpleListProperty<>();
    }

    public long getId() {
        return id.get();
    }

    public SimpleLongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Category getCategory() {
        return category.get();
    }

    public SimpleObjectProperty<Category> categoryProperty() {
        return category;
    }

    public void setCategory(Category category) {
        this.category.set(category);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public ObservableList<String> getAllergens() {
        return allergens.get();
    }

    public SimpleListProperty<String> allergensProperty() {
        return allergens;
    }

    public void setAllergens(ObservableList<String> allergens) {
        this.allergens.set(allergens);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", price=" + price +
                ", category=" + category +
                ", allergens=" + allergens +
                '}';
    }
}
