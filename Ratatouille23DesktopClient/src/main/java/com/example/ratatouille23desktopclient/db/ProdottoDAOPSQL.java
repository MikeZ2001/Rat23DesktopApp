package com.example.ratatouille23desktopclient.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class ProdottoDAOPSQL {
    private Connection connection;

    private PreparedStatement insertProduct, getProductsByName;

    public ProdottoDAOPSQL(Connection connection) throws SQLException {
        this.connection = connection;

        this.insertProduct = connection.prepareStatement("INSERT INTO Product(name, last_modified) VALUES (?,?) ON CONFLICT DO NOTHING");
        this.getProductsByName = connection.prepareStatement("SELECT * FROM Product prod WHERE LOWER(prod.name) LIKE '%' || LOWER(?) || '%'");
    }


    public void insertProduct(String product) throws SQLException{
        if (product.length() > 0){
            String prodName = product.substring(0,1).toUpperCase() + product.substring(1, product.length()).toLowerCase();
            insertProduct.setString(1, prodName);
            Date lastModified = new Date();
            insertProduct.setDate(2, new java.sql.Date(lastModified.getTime()));

            insertProduct.executeUpdate();
        }
    }

    public ArrayList<String> getProductsByName(String term) throws SQLException {
        ArrayList<String> products = new ArrayList<>();
        getProductsByName.setString(1, term);
        ResultSet results = getProductsByName.executeQuery();

        while (results.next()){
            products.add(results.getString(1));
        }

        results.close();

        return products;
    }
}
