package com.example.ratatouille23desktopclient.model;

import com.example.ratatouille23desktopclient.model.enums.Role;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class Employee {
    @SerializedName("id")
    private SimpleLongProperty id;
    @SerializedName("name")
    private SimpleStringProperty name;
    @SerializedName("surname")
    private SimpleStringProperty surname;
    @SerializedName("role")
    private SimpleObjectProperty<Role> role;

    @SerializedName("email")
    private SimpleStringProperty email;

    @SerializedName("store")
    private SimpleObjectProperty<Store> store;

    public Employee() {
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty();
        this.surname = new SimpleStringProperty();
        this.role = new SimpleObjectProperty<>();
        this.email = new SimpleStringProperty();
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

    public String getSurname() {
        return surname.get();
    }

    public SimpleStringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public Role getRole() {
        return role.get();
    }

    public SimpleObjectProperty<Role> roleProperty() {
        return role;
    }

    public void setRole(Role role) {
        this.role.set(role);
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
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
        if (!(o instanceof Employee that)) return false;
        return Objects.equals(id.get(), that.id.get()) && Objects.equals(email.get(), that.email.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get(), email.get());
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name=" + name +
                ", surname=" + surname +
                ", role=" + role +
                ", email=" + email +
                ", store=" + store +
                '}';
    }
}
