package com.example.ratatouille23desktopclient.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {
    private static DBManager instance;
    private Connection connection = null;

    private final String username = "postgres";
    private final String password = "postgres";
    private final String url = "jdbc:postgresql://localhost:5432/";

    private final String nomeDB = "offdata";

    private final String DB_ESISTENTE = "42P04";

    private DBManager() throws SQLException {
        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);
            creaDB();
            connection = DriverManager.getConnection(url + nomeDB, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        return connection;
    }

    public static DBManager getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()){
            instance = new DBManager();
        }
        return instance;
    }

    private void creaDB(){
        try{
            Statement statement = null;
            statement = connection.createStatement();
            int res = statement.executeUpdate("CREATE DATABASE "+nomeDB);
            statement.close();
            connection.close();
        }catch (SQLException sqlException){
            if (!sqlException.getSQLState().equals(DB_ESISTENTE)){
                sqlException.printStackTrace();
            }
        }
    }
}
