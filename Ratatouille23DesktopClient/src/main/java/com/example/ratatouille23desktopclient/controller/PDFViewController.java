package com.example.ratatouille23desktopclient.controller;

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import com.dansoftware.pdfdisplayer.PdfJSVersion;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CheckGenerator;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Order;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class PDFViewController {
    @FXML
    private VBox mainVBox;
    @FXML
    private Button closeButton, printButton;
    private PDFDisplayer pdfDisplayer;
    private Order order;

    private File checkFile;

    private final Logger logger = CustomLogger.getLogger();

    public void initialize(Order order){

        logger.info("Inizializzazione schermata Visualizzazione PDF Conto.");
        this.order = order;
        initializePDFDisplayer();
        initializeButtons();
        createCheck();
        initializeOnCloseEvent();

        logger.info("Terminata inizializzazione schermata Visualizzazione PDF Conto.");
    }

    private void initializeButtons() {
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Click su Chiudi. Reindirizzamento alla schermata Dettagli Conto del tavolo.");
                closePDFView();
            }
        });

        printButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Click su Stampa.");
                logger.info("Avvio procedura di stampa del conto.");
                printCheck();
            }
        });
    }

    private void initializePDFDisplayer() {
        logger.info("Inizializzazione pdf displayer.");
        pdfDisplayer = new PDFDisplayer(PdfJSVersion._2_2_228);
        WebView pdfDisplayerWebView = (WebView) pdfDisplayer.toNode();
        pdfDisplayerWebView.setPrefHeight(1080);
        pdfDisplayerWebView.setPrefWidth(1920);

        mainVBox.getChildren().add(pdfDisplayerWebView);
        logger.info("Terminata inizializzazione pdf displayer.");
    }

    private void createCheck(){
        logger.info("Creazione file conto pdf temporaneo.");
        try {
            CheckGenerator checkGenerator = new CheckGenerator(order);
            this.checkFile = checkGenerator.generate();
            logger.info("Terminata creazione file conto pdf temporaneo. Caricamento del file nel displayer.");
            loadFileInWebView();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            handleGenericExceptions();
        }
    }

    private void loadFileInWebView() {
        Platform.runLater(() -> {
            try {
                pdfDisplayer.loadPDF(checkFile);
                logger.info("Caricamento del file pdf nel displayer completato.");
            } catch (IOException e) {
                logger.severe(e.getMessage());
                handleGenericExceptions();
            }
        });
    }

    private void initializeOnCloseEvent(){
        ((Stage) closeButton.getScene().getWindow()).setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                closePDFView();
            }
        });
    }

    private void closePDFView(){
        logger.info("Eliminazione del file pdf temporaneo. Reindirizzamento alla schermata Dettagli Conto del tavolo.");
        checkFile.delete();
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    private void printCheck() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob.showPageSetupDialog(printButton.getScene().getWindow())){
            logger.info("Mostrato dialog per set up della pagina di stampa.");
            if (printerJob.showPrintDialog(printButton.getScene().getWindow())){
                logger.info("Mostrato dialog per selezione della stampante e formattazione della stampa.");
                ((WebView) pdfDisplayer.toNode()).getEngine().print(printerJob);
                printerJob.endJob();
                logger.info("Terminata procedura di stampa del conto.");
            }
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
