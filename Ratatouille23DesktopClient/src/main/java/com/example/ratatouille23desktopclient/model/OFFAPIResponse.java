package com.example.ratatouille23desktopclient.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OFFAPIResponse {
    @SerializedName("count")
    private int count;

    @SerializedName("page")
    private int page;

    @SerializedName("page_size")
    private int size;

    @SerializedName("products")
    private List<OFFProduct> products;

    public int getCount() {
        return count;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public List<OFFProduct> getProducts() {
        return products;
    }
}
