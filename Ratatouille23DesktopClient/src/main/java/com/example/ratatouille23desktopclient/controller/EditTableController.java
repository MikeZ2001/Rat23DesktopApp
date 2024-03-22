package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Table;
import com.example.ratatouille23desktopclient.viewmodel.TableVM;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class EditTableController {
    @FXML
    private Button cancelButton, saveButton;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField nameTextField, seatsNumberTextField;

    private Table table;
    private TableVM tableVM;

    private final Logger logger = CustomLogger.getLogger();

    public void initialize(Table table, TableVM tableVM){

        logger.info("Inizializzazione dialog per creazione/modifica di un tavolo.");
        this.table = table;
        this.tableVM = tableVM;

        if (table != null){
            titleLabel.setText("Modifica " + table.getName());
            nameTextField.setText(table.getName());
            seatsNumberTextField.setText(String.valueOf(table.getSeatsNumber()));
        }else {
            titleLabel.setText("Crea Tavolo");
        }
        logger.info("Terminata inizializzazione dialog per creazione/modifica di un tavolo.");
    }

    @FXML
    private void cancelEditing(){
        logger.info("Click su Annulla.");
        logger.info("Reindirizzamento alla schermata dei tavoli.");
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void saveEdits(){
        logger.info("Click su Salva.");
        if (nameTextField.getText().isEmpty()){
            logger.info("L'utente non ha inserito il nome del tavolo da salvare.");
            handleEmptyFields();
        }
        else {
            String name = String.valueOf(nameTextField.getText());
            try{
                int seats = Integer.valueOf(seatsNumberTextField.getText());

                if (table == null)
                    table = new Table();
                table.setName(name);
                table.setSeatsNumber(seats);

                logger.info("Avvio procedura salvataggio delle modifiche.");
                tableVM.updateTavoli(table, () -> updateSuccessFeedback(), () -> saveSuccessFeedback());
                ((Stage) saveButton.getScene().getWindow()).close();
            } catch (NumberFormatException nfe){
                logger.info("Il numero di posti inserito dall'utente non è un numero valido.");
                handleNotANumber();
            } catch (Exception e){
                logger.severe(e.getMessage());
                handleGenericExceptions();
            }
        }
    }

    //FEEDBACK E ECCEZIONI
    //----------------------------------------------
    private void updateSuccessFeedback(){
        logger.info("Terminata procedura salvataggio delle modifiche. Reindirizzamento alla schermata dei tavoli.");
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Modifica completata")
                .setMessage("Il tavolo è stato aggiornato.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void saveSuccessFeedback(){
        logger.info("Terminata procedura salvataggio delle modifiche. Reindirizzamento alla schermata dei tavoli.");
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Creazione completata")
                .setMessage("Il tavolo è stato creato.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleNotANumber(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Attenzione, il numero di posti inseriti non è un numero accettabile.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleGenericExceptions(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Qualcosa è andato storto. Riprovare.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleEmptyFields(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Inserire prima almeno il nome del tavolo.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
