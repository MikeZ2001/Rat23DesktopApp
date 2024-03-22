package com.example.ratatouille23desktopclient.model;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.Objects;

public class Table {
    @SerializedName("id")
    private SimpleLongProperty id;

    @SerializedName("name")
    private SimpleStringProperty name;
    @SerializedName("seatsNumber")
    private SimpleIntegerProperty seatsNumber;
    @SerializedName("available")
    private SimpleBooleanProperty available;

    @SerializedName("store")
    private SimpleObjectProperty<Store> store;

    public Table(){
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty();
        this.seatsNumber = new SimpleIntegerProperty();
        this.available = new SimpleBooleanProperty(true);
        this.store = new SimpleObjectProperty<>();
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

    public int getSeatsNumber() {
        return seatsNumber.get();
    }

    public SimpleIntegerProperty seatsNumberProperty() {
        return seatsNumber;
    }

    public void setSeatsNumber(int seatsNumber) {
        this.seatsNumber.set(seatsNumber);
    }

    public boolean getAvailable() {
        return available.get();
    }

    public SimpleBooleanProperty availableProperty() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available.set(available);
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

    public boolean isAvailable() {
        return available.get();
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
        if (!(o instanceof Table)) return false;
        Table table = (Table) o;
        return id.equals(table.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", name=" + name +
                ", seatsNumber=" + seatsNumber +
                ", available=" + available +
                ", store=" + store +
                '}';
    }
}
