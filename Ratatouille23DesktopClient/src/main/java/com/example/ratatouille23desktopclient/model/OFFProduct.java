package com.example.ratatouille23desktopclient.model;

import com.google.gson.annotations.SerializedName;

public class OFFProduct {
    @SerializedName("product_name")
    private String nome;

    public String getName() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "OFFProduct{" +
                "nome='" + nome + '\'' +
                '}';
    }
}
