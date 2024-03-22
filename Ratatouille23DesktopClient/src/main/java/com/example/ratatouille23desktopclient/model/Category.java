package com.example.ratatouille23desktopclient.model;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.Objects;

public class Category {
    private SimpleLongProperty id;

    private SimpleStringProperty name;
    private SimpleStringProperty description;
    private SimpleObjectProperty<Integer> numberOfItems;

    private SimpleListProperty<Product> productsOfTheCategory;

    private SimpleObjectProperty<Store> store;

    public Category() {
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.numberOfItems = new SimpleObjectProperty<>();
        this.productsOfTheCategory = new SimpleListProperty<>();
        this.store = new SimpleObjectProperty<>();
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

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Integer getNumberOfItems() {
        return numberOfItems.get();
    }

    public SimpleObjectProperty<Integer> numberOfItemsProperty() {
        return numberOfItems;
    }

    public void setNumberOfItems(Integer numberOfItems) {
        this.numberOfItems.set(numberOfItems);
    }

    public ObservableList<Product> getProductsOfTheCategory() {
        return productsOfTheCategory.get();
    }

    public SimpleListProperty<Product> productsOfTheCategoryProperty() {
        return productsOfTheCategory;
    }

    public void setProductsOfTheCategory(ObservableList<Product> productsOfTheCategory) {
        this.productsOfTheCategory.set(productsOfTheCategory);
    }

    public Store getStore() {
        return store.get();
    }

    public SimpleObjectProperty<Store> storeProperty() {
        return store;
    }

    public void setStore(Store store) {
        this.store.set(store);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category category)) return false;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", numberOfItems=" + numberOfItems +
                ", productsOfTheCategory=" + productsOfTheCategory +
                ", store=" + store +
                '}';
    }
}
