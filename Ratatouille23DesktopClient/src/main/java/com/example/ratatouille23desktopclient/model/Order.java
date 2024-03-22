package com.example.ratatouille23desktopclient.model;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.Objects;

public class Order implements Serializable {
    public enum Status{
        ACCEPTED, IN_PROGRESS, READY, COMPLETED, CANCELLED, PAYED;

        public String stringValue(){
            if (name().equals(ACCEPTED.name()))
                return "Accettato";
            else if (name().equals(IN_PROGRESS.name()))
                return "In Corso";
            else if (name().equals(READY.name()))
                return "Pronto";
            else if (name().equals(COMPLETED.name()))
                return "Completato";
            else if (name().equals(CANCELLED.name()))
                return "Annullato";
            else
                return "Pagato";
        }
    }

    @SerializedName("id")
    private SimpleLongProperty id;
    @SerializedName("status")
    private SimpleObjectProperty<Status> status;
    @SerializedName("date")
    private SimpleStringProperty date;
    @SerializedName("time")
    private SimpleStringProperty time;
    @SerializedName("total")
    private SimpleDoubleProperty total;
    @SerializedName("notes")
    private SimpleStringProperty notes;

    @SerializedName("table")
    private SimpleObjectProperty<Table> table;

    @SerializedName("employeesOfTheOrder")
    private SimpleListProperty<Employee> employeesOfTheOrder;

    @SerializedName("items")
    private SimpleListProperty<OrderItem> items;

    public Order(){
        this.id = new SimpleLongProperty();
        this.date = new SimpleStringProperty();
        this.time = new SimpleStringProperty();
        this.total = new SimpleDoubleProperty();
        this.status = new SimpleObjectProperty<>();
        this.notes = new SimpleStringProperty();
        this.table = new SimpleObjectProperty<>();
        this.employeesOfTheOrder = new SimpleListProperty<>();
        this.items = new SimpleListProperty<>();
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

    public Status getStatus() {
        return status.get();
    }

    public SimpleObjectProperty<Status> statusProperty() {
        return status;
    }

    public void setStatus(Status status) {
        this.status.set(status);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public double getTotal() {
        return total.get();
    }

    public SimpleDoubleProperty totalProperty() {
        return total;
    }

    public void setTotal(double total) {
        this.total.set(total);
    }

    public String getNotes() {
        return notes.get();
    }

    public SimpleStringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public Table getTable() {
        return table.get();
    }

    public SimpleObjectProperty<Table> tableProperty() {
        return table;
    }

    public void setTable(Table table) {
        this.table.set(table);
    }

    public ObservableList<Employee> getEmployeesOfTheOrder() {
        return employeesOfTheOrder.get();
    }

    public SimpleListProperty<Employee> employeesOfTheOrderProperty() {
        return employeesOfTheOrder;
    }

    public void setEmployeesOfTheOrder(ObservableList<Employee> employeesOfTheOrder) {
        this.employeesOfTheOrder.set(employeesOfTheOrder);
    }

    public ObservableList<OrderItem> getItems() {
        return items.get();
    }

    public SimpleListProperty<OrderItem> itemsProperty() {
        return items;
    }

    public void setItems(ObservableList<OrderItem> items) {
        this.items.set(items);
    }

    public boolean isCurrent(){
        return !status.get().equals(Status.COMPLETED) && !status.get().equals(Status.PAYED) && !status.get().equals(Status.CANCELLED);
    }

    @Override
    public String toString() {
        return "Ordine{" +
                "id=" + id +
                ", status=" + status +
                ", date=" + date +
                ", time=" + time +
                ", total=" + total +
                ", notes=" + notes +
                ", table=" + table +
                ", employees=" + employeesOfTheOrder +
                ", items=" + items +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(id.get(), order.id.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }
}
