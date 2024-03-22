package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Category;
import com.example.ratatouille23desktopclient.viewmodel.CategoryVM;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class EditCategoryController {

    @FXML
    private Button saveButton, cancelButton;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Label titleLabel;

    private Category category;
    private CategoryVM categoryVM;

    private final Logger logger = CustomLogger.getLogger();

    public void initialize(Category category, CategoryVM categoryVM){

        logger.info("Inizializzazione dialog per creazione/modifica di una categoria.");
        this.category = category;
        this.categoryVM = categoryVM;

        if (category == null){
            titleLabel.setText("Crea Categoria");
        }else{
            titleLabel.setText("Modifica Categoria");
            nameTextField.setText(category.getName());
            descriptionTextArea.setText(category.getDescription());
        }
        logger.info("Terminata inizializzazione dialog per creazione/modifica di una categoria.");
    }

    @FXML
    private void cancelEdits(){
        logger.info("Click su Annulla. Reindirizzamento alla schermata delle categorie.");
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void saveEdits(){
        logger.info("Click su Salva.");
        if (nameTextField.getText().isEmpty()){
            handleEmptyField();
            logger.info("L'utente non ha inserito il nome della categoria. Impossibile proseguire oltre.");
        } else{
            if (category == null)
                category = new Category();

            category.setName(String.valueOf(nameTextField.getText()));
            category.setDescription(String.valueOf(descriptionTextArea.getText()));

            logger.info("Avvio procedura per salvataggio delle modifiche lato server.");
            categoryVM.updateCategories(category, () -> saveSuccessFeedback(), () -> updateSuccessFeedback());

            logger.info("Reindirizzamento alla schermata delle categorie.");
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    //FEEDBACK E ECCEZIONI
    //----------------------------------------------
    private void updateSuccessFeedback(){
        logger.info("Terminata procedura per salvataggio delle modifiche lato server.");
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Modifica completata")
                .setMessage("La categoria è stata aggiornata.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void saveSuccessFeedback(){
        logger.info("Terminata procedura per salvataggio delle modifiche lato server.");
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Salvataggio completato")
                .setMessage("La categoria è stata creata.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleEmptyField() {
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Inserire prima almeno il nome della categoria.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
