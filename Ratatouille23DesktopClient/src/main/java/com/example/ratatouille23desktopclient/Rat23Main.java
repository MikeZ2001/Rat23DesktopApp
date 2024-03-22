package com.example.ratatouille23desktopclient;

import com.example.ratatouille23desktopclient.db.DBHelper;
import com.example.ratatouille23desktopclient.db.DBManager;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Rat23Main extends Application {
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void start(Stage stage) throws IOException {
        final Logger logger = CustomLogger.getLogger();



        FXMLLoader fxmlLoader = new FXMLLoader(Rat23Main.class.getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);

        stage.setResizable(false);
        stage.setTitle("Ratatouille23");
        stage.setScene(scene);
        stage.setFullScreen(true);

        stage.show();

        try {
            Connection connection = DBManager.getInstance().getConnection();
            DBHelper dbHelper = new DBHelper(connection);
            dbHelper.createProductTable();
            logger.info("Database locale dei suggerimenti connesso/creato.");
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}