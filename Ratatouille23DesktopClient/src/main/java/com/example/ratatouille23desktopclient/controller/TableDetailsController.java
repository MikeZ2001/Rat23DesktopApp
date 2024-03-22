package com.example.ratatouille23desktopclient.controller;


import com.example.ratatouille23desktopclient.Rat23Main;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.OrderItem;
import com.example.ratatouille23desktopclient.model.Table;
import com.example.ratatouille23desktopclient.viewmodel.OrderVM;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class TableDetailsController {

    @FXML
    private Label titleLabel;

    @FXML
    private Button printCheckButton, payedButton;

    @FXML
    private TableView<OrderItem> productsTableView;
    @FXML
    private TableColumn<OrderItem, Long> productIdCol;
    @FXML
    private TableColumn<OrderItem, String> productNameCol, productNotesCol;

    @FXML
    private TableColumn<OrderItem, Integer> productQuantityCol;

    @FXML
    private TableColumn<OrderItem, Number> productPriceCol;

    @FXML
    private TableColumn<OrderItem, Number> productTotalCol;
    private OrderVM orderVM;
    private Table table;

    private final Logger logger = CustomLogger.getLogger();

    public void initialize(Table table){

        logger.info("Inizializzazione schermata Dettagli Conto del tavolo.");
        this.table = table;

        orderVM = new OrderVM();

        logger.info("Avvio procedura ottenimento conto attuale del tavolo.");
        orderVM.getTableOrder(table, () -> {
            logger.info("Terminata procedura ottenimento conto attuale del tavolo.");
            initializeTitle();
            initializeItemsTable();
        });

        logger.info("Terminata inizializzazione schermata Dettagli Conto del tavolo.");
    }

    private void initializeTitle() {
        titleLabel.setText("Riepilogo " + table.getName() + " (Totale: " + orderVM.currentOrderProperty().get().getTotal() + "€)" +
                "\tStato: " + orderVM.currentOrderProperty().get().getStatus().stringValue());
    }

    private void initializeItemsTable() {
        logger.info("Inizializzazione tabella prodotti nel conto.");
        productsTableView.itemsProperty().bind(orderVM.currentOrderProperty().get().itemsProperty());

        productIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        productNameCol.setCellValueFactory(data -> data.getValue().getProduct().nameProperty());

        productQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        productPriceCol.setCellValueFactory(data -> data.getValue().getProduct().priceProperty());
        productPriceCol.setCellFactory(tc -> new TableCell<>(){
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (value == null || empty){
                    setText(null);
                    setStyle("");
                }else{
                    setText(value + "€");
                }
            }
        });

        productTotalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getQuantity()*data.getValue().getProduct().getPrice()));
        productTotalCol.setCellFactory(tc -> new TableCell<>(){
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (value == null || empty){
                    setText(null);
                    setStyle("");
                }else{
                    setText(value + "€");
                }
            }
        });

        productNotesCol.setCellValueFactory(new PropertyValueFactory<>("particularRequests"));

        logger.info("Terminata inizializzazione tabella prodotti nel conto.");
    }

    @FXML
    private void payed(){
        logger.info("Click su Pagato.");
        logger.info("Avvio procedura pagamento del conto lato server.");
        orderVM.payOrder(table, this::paymentSuccess);
        ((Stage) payedButton.getScene().getWindow()).close();
    }

    @FXML
    private void printCheck(){
        logger.info("Click su Stampa Conto. Reindirizzamento alla schermata di visualizzazione del conto pdf.");
        goToPDFWebView();
    }

    private void paymentSuccess(){
        logger.info("Terminata procedura pagamento del conto lato server. Reindirizzamento alla schermata dei tavoli.");
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Pagato")
                .setMessage("L'ordine è stato aggiornato.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void goToPDFWebView(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Rat23Main.class.getResource("pdfView.fxml"));
            Parent root = fxmlLoader.load();
            Stage window = (Stage) printCheckButton.getScene().getWindow();
            PDFViewController controller = fxmlLoader.getController();
            window.setScene(new Scene(root, 1920, 1080));
            window.setResizable(false);
            window.setFullScreen(false);
            controller.initialize(orderVM.currentOrderProperty().get());
            window.setTitle("PDF Conto " + orderVM.currentOrderProperty().get().getId());
        } catch (IOException ioe){
            logger.severe(ioe.getMessage());
            handleGenericExceptions();
        }
    }

    private void handleGenericExceptions(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Qualcosa è andato storto. Riprovare.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
