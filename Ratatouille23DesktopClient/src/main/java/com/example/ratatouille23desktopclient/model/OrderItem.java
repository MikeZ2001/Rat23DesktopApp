package com.example.ratatouille23desktopclient.model;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class OrderItem {

    public enum OrderItemStatus{
        READY, NOT_READY
    }

    @SerializedName("id")
    private SimpleLongProperty id;
    @SerializedName("quantity")
    private SimpleIntegerProperty quantity;
    @SerializedName("particularRequests")
    private SimpleStringProperty particularRequests;
    @SerializedName("product")
    private SimpleObjectProperty<Product> product;
    @SerializedName("order")
    private SimpleObjectProperty<Order> order;
    @SerializedName("orderItemStatus")
    private SimpleObjectProperty<OrderItemStatus> orderItemStatus;

    public OrderItem(){
        this.id = new SimpleLongProperty();
        this.quantity = new SimpleIntegerProperty();
        this.particularRequests = new SimpleStringProperty();
        this.product = new SimpleObjectProperty<>();
        this.order = new SimpleObjectProperty<>();
        this.orderItemStatus = new SimpleObjectProperty<>();
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

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public String getParticularRequests() {
        return particularRequests.get();
    }

    public SimpleStringProperty particularRequestsProperty() {
        return particularRequests;
    }

    public void setParticularRequests(String particularRequests) {
        this.particularRequests.set(particularRequests);
    }

    public Product getProduct() {
        return product.get();
    }

    public SimpleObjectProperty<Product> productProperty() {
        return product;
    }

    public void setProduct(Product product) {
        this.product.set(product);
    }

    public Order getOrder() {
        return order.get();
    }

    public SimpleObjectProperty<Order> orderProperty() {
        return order;
    }

    public void setOrder(Order order) {
        this.order.set(order);
    }

    public OrderItemStatus getOrderItemStatus() {
        return orderItemStatus.get();
    }

    public SimpleObjectProperty<OrderItemStatus> orderItemStatusProperty() {
        return orderItemStatus;
    }

    public void setOrderItemStatus(OrderItemStatus orderItemStatus) {
        this.orderItemStatus.set(orderItemStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem that = (OrderItem) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OrdineItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", particularRequests=" + particularRequests +
                ", product=" + product +
                ", order=" + order +
                ", orderItemStatus=" + orderItemStatus +
                '}';
    }
}
