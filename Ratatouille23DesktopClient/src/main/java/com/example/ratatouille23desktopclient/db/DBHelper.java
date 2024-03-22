package com.example.ratatouille23desktopclient.db;

import java.sql.*;

public class DBHelper {
    private Connection connection;

    public DBHelper(Connection connection){
        this.connection = connection;
    }

    private boolean connectionExists() {
        return !(connection == null);
    }

    public int createProductTable() throws SQLException{
        int risultato = -1;
        if(connectionExists()) {
            Statement statement = connection.createStatement();
            String createTable = "CREATE TABLE IF NOT EXISTS Product(\r\n"
                    + "	name varchar(150) PRIMARY KEY,\r\n"
                    + " last_modified DATE);";
            risultato = statement.executeUpdate(createTable);
            statement.close();
            connection.close();
        }
        return risultato;
    }
}
