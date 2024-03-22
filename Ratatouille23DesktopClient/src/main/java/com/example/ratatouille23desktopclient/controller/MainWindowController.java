package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.Rat23Main;
import com.example.ratatouille23desktopclient.aws.auth.AuthController;
import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.model.enums.Role;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainWindowController implements Initializable {

    @FXML
    private VBox fragment;

    @FXML
    private Label usernameLabel, userRoleLabel;

    @FXML
    private ToggleGroup leftMenuToggleGroup;

    @FXML
    private ToggleButton storeToggleButton, tablesToggleButton, categoriesToggleButton, employeesToggleButton, productsToggleButton, statisticToggleButton, logoutButton;

    private Employee currentUser;

    private final Logger logger = CustomLogger.getLogger();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        logger.info("Inizializzazione schermata principale.");
        logoutButton.setOnAction(e -> logout());
        initializeUserInfo();
        initializeLeftMenuToggleButtons();
        goToStoreView();
        logger.info("Schermata principale configurata e inizializzata.");
    }

    private void initializeLeftMenuToggleButtons(){
        storeToggleButton.setUserData("Attività");
        tablesToggleButton.setUserData("Tavoli");
        categoriesToggleButton.setUserData("Categorie");
        employeesToggleButton.setUserData("Dipendenti");
        productsToggleButton.setUserData("Prodotti");
        statisticToggleButton.setUserData("Statistiche");
        logoutButton.setUserData("Logout");

        logger.info("Inizializzazione del menu a sinistra.");
        if (currentUser.getRole().equals(Role.WAITER) || currentUser.getRole().equals(Role.CHEF)){
            storeToggleButton.setDisable(true);
            tablesToggleButton.setDisable(true);
            categoriesToggleButton.setDisable(true);
            employeesToggleButton.setDisable(true);
            productsToggleButton.setDisable(true);
            statisticToggleButton.setDisable(true);
        } else if (currentUser.getRole().equals(Role.SUPERVISOR)){
            employeesToggleButton.setDisable(true);
            statisticToggleButton.setDisable(true);
        } else{
            tablesToggleButton.setDisable(false);
            categoriesToggleButton.setDisable(false);
            employeesToggleButton.setDisable(false);
            productsToggleButton.setDisable(false);
            statisticToggleButton.setDisable(false);
        }
        leftMenuToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldValue, Toggle newValue) {
                if (newValue == null){
                    oldValue.setSelected(true);
                }else{
                    logger.info("Click su " + newValue.getUserData().toString());
                }
            }
        });
        logger.info("Terminata inizializzazione del menu a sinistra.");
    }

    private void initializeUserInfo(){
        logger.info("Inizializzazione informazioni account.");
        currentUser = new Employee();
        RAT23Cache<String, String> cache = RAT23Cache.getCacheInstance();
        currentUser.setName(cache.get("currentUserGivenName"));
        currentUser.setSurname(cache.get("currentUserFamilyName"));
        currentUser.setRole(Role.valueOf(cache.get("currentUserRole")));
        usernameLabel.setText(currentUser.getName() + " " + currentUser.getSurname());
        userRoleLabel.setText(currentUser.getRole().toString());
        logger.info("Terminato inizializzazione informazioni account.");
    }

    private void logout(){
        logger.info("Click su Logout.");
        try {
            AuthController authController = new AuthController();
            logger.info("Avvio procedura di logout.");
            authController.logout();
            logger.info("Logout terminato. Reindirizzamento alla schermata di login.");
            goToLoginView();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            handleOtherAuthException();
        }
    }

    //NAVIGAZIONE VERSO ALTRE FINESTRE
    //----------------------------------------------------------
    @FXML
    private void goToTablesView(){
        try {
            fragment.getChildren().clear();
            fragment.getChildren().add(FXMLLoader.load(Rat23Main.class.getResource("tablesFragment.fxml")));
        } catch (IOException e) {
            logger.severe(e.getMessage());
            handleGenericExceptions();
        }
    }

    @FXML
    private void goToStoreView(){
        try {
            fragment.getChildren().clear();
            fragment.getChildren().add(FXMLLoader.load(Rat23Main.class.getResource("storeFragment.fxml")));
        } catch (IOException e) {
            logger.severe(e.getMessage());
            handleGenericExceptions();
        }
    }

    @FXML
    private void goToCategoriesView(){
        try {
            fragment.getChildren().clear();
            fragment.getChildren().add(FXMLLoader.load(Rat23Main.class.getResource("categoriesFragment.fxml")));
        } catch (IOException e) {
            logger.severe(e.getMessage());
            handleGenericExceptions();
        }
    }

    @FXML
    private void goToProductsView(){
        try {
            fragment.getChildren().clear();
            fragment.getChildren().add(FXMLLoader.load(Rat23Main.class.getResource("productsFragment.fxml")));
        } catch (IOException e) {
            logger.severe(e.getMessage());
            handleGenericExceptions();
        }
    }

    @FXML
    private void goToEmployeesView(){
        try {
            fragment.getChildren().clear();
            fragment.getChildren().add(FXMLLoader.load(Rat23Main.class.getResource("employeesFragment.fxml")));
        } catch (IOException e) {
            logger.severe(e.getMessage());
            handleGenericExceptions();
        }
    }

    @FXML
    private void goToStatisticsView(){
        try {
            fragment.getChildren().clear();
            fragment.getChildren().add(FXMLLoader.load(Rat23Main.class.getResource("statisticsFragment.fxml")));
        } catch (IOException e) {
            logger.severe(e.getMessage());
            handleGenericExceptions();
        }
    }

    private void goToLoginView(){
        try{
            Parent root = FXMLLoader.load(Rat23Main.class.getResource("loginView.fxml"));
            Stage window = (Stage) logoutButton.getScene().getWindow();
            window.setScene(new Scene(root, 1920, 1080));
            window.setResizable(false);
            window.setFullScreen(true);
        } catch (IOException ioe){
            logger.severe(ioe.getMessage());
            handleGenericExceptions();
        }
    }

    //FEEDBACK E ECCEZIONI
    //----------------------------------------------
    private void handleGenericExceptions(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Qualcosa è andato storto. Riprovare.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleOtherAuthException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Logout fallito")
                .setMessage("Qualcosa è andato storto durante il logout. Riprovare.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
