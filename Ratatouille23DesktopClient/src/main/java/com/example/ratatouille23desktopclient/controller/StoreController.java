package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.viewmodel.StoreVM;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.logging.Logger;

public class StoreController {
    @FXML
    private Button editButton, cancelButton, saveButton;

    @FXML
    private TextField storeNameTextField, storeEmailTextField, storeAddressTextField, storePhoneTextField;
    private StoreVM storeVM;

    private final Logger logger = CustomLogger.getLogger();

    public void initialize(){

        logger.info("Inizializzazione del fragment Attività.");
        storeVM = new StoreVM();

        storeNameTextField.textProperty().bindBidirectional(storeVM.storeNameProperty());
        storeEmailTextField.textProperty().bindBidirectional(storeVM.storeEmailProperty());
        storePhoneTextField.textProperty().bindBidirectional(storeVM.storePhoneProperty());
        storeAddressTextField.textProperty().bindBidirectional(storeVM.storeAddressProperty());

        logger.info("Avvio procedura ottenimento informazioni attività dal server.");
        storeVM.getCurrentStore();
        logger.info("Terminata procedura ottenimento informazioni attività dal server.");

        logger.info("Terminata inizializzazione del fragment Attività.");
    }

    @FXML
    private void startEditingStore(){
        logger.info("Click su Modifica. Avvio modalità modifica attività.");
        storeNameTextField.setEditable(true);
        storeEmailTextField.setEditable(true);
        storeAddressTextField.setEditable(true);
        storePhoneTextField.setEditable(true);

        editButton.setDisable(true);
        cancelButton.setVisible(true);
        saveButton.setVisible(true);
    }

    @FXML
    private void cancelEditingStore(){
        logger.info("Click su Annulla. Annulla modalità modifica attività e relative modifiche.");
        cancelButton.setVisible(false);
        saveButton.setVisible(false);

        storeNameTextField.setEditable(false);
        storeEmailTextField.setEditable(false);
        storeAddressTextField.setEditable(false);
        storePhoneTextField.setEditable(false);

        editButton.setDisable(false);
        storeVM.getCurrentStore();
    }

    @FXML
    private void saveEditsStore(){
        logger.info("Click su Salva. Salvataggio modifiche.");
        if (storeNameTextField.getText().isEmpty()){
            handleEmptyField();
            logger.info("L'utente non ha inserito il nome dell'attività. Impossibile proseguire.");
        }
        else {
            cancelButton.setVisible(false);
            saveButton.setVisible(false);

            storeNameTextField.setEditable(false);
            storeEmailTextField.setEditable(false);
            storeAddressTextField.setEditable(false);
            storePhoneTextField.setEditable(false);

            editButton.setDisable(false);
            logger.info("Avvio procedura salvataggio modifiche attività lato server.");
            storeVM.saveStore(this::savedChanges);
        }
    }


    //FEEDBACK E ECCEZIONI
    //----------------------------------------------
    private void handleEmptyField(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Inserire prima il nome dell'esercizio.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void savedChanges(){
        logger.info("Terminata procedura salvataggio modifiche attività lato server.");
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Modifica completata")
                .setMessage("L'esercizio è stato aggiornato correttamente.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
