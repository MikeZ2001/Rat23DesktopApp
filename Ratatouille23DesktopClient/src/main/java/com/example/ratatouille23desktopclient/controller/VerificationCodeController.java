package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.Rat23Main;
import com.example.ratatouille23desktopclient.aws.auth.AuthController;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.model.Store;
import com.example.ratatouille23desktopclient.viewmodel.EmployeeVM;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class VerificationCodeController {
    @FXML
    private Button confirmCodeButton;

    @FXML
    private Button resendButton;

    @FXML
    private TextField verificationCodeTextField;
    private Stage sourceStage;
    private EmployeeVM employeeVM;

    private final Logger logger = CustomLogger.getLogger();

    public void initialize(String email, Stage sourceStage) {
        logger.info("Configurazione e avvio del dialog per la conferma dell'account.");
        this.sourceStage = sourceStage;
        this.employeeVM = new EmployeeVM();

        confirmCodeButton.setOnAction(e -> confirmCode(email));
        resendButton.setOnAction(e -> resendCode(email));

        logger.info("Dialog per la conferma dell'account configurato e avviato.");
    }

    private void confirmCode(String email){
        logger.info("Click su Conferma.");
        if (verificationCodeTextField.getText().isEmpty()){
            logger.info("L'utente non ha inserito il codice di conferma.");
            handleEmptyCodeField();
        }else{
            String verificationCode = String.valueOf(verificationCodeTextField.getText());
            AuthController authController = new AuthController();
            try {
                logger.info("Avvio procedura di conferma dell'account tramite Cognito.");
                authController.confirmCode(email, verificationCode);
                logger.info("Procedura di conferma dell'account terminata.");
                logger.info("Avvio procedura ottenimento dati dell'utente.");
                Employee employee = authController.getUserByEmail(email);
                logger.info("Procedura ottenimento dati utente terminata.");
                Store store = new Store();
                createStore(store);
                employee.setStore(store);
                logger.info("Avvio procedura registrazione utente lato server.");
                employeeVM.registerAdmin(employee, () -> {
                    signUpCompleteFeedback();
                    logger.info("Procedura registrazione utente lato server terminata. Reindirizzamento alla finestra di login.");
                    goToLoginView();
                });
            }catch (Exception e){
                handleOtherAuthExceptions();
                logger.severe(e.getMessage());
            }
        }
    }

    private void resendCode(String email){
        logger.info("Click su Invia di nuovo.");
        try{
            AuthController authController = new AuthController();
            logger.info("Avvio procedura reinvio del codice di conferma dell'account.");
            authController.resendCode(email);
            logger.info("Procedura reinvio codice di conferma dell'account terminata.");
        }catch (Exception e){
            logger.severe(e.getMessage());
            handleResendCodeException();
        }
    }

    private void createStore(Store store){
        logger.info("Avvio creazione dell'attività per registrazione dell'amministratore.");
        VBox content = new VBox();
        Label instructions = new Label("Prima di proseguire insesci il nome della propria attività." +
                "\nIn seguito potrai modificare il nome e aggiungere ulteriori informazioni nella finestra Attività.");
        TextField storeTextField = new TextField();
        storeTextField.setPromptText("Nome dell'attività");
        content.getChildren().addAll(instructions, storeTextField);

        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Nuova attività")
                .setContent(content)
                .setOkButton("Ok")
                .build();

        dialog.setOkEventHandler(event -> {
            if (!storeTextField.getText().isEmpty()){
                String storeName = String.valueOf(storeTextField.getText());
                store.setName(storeName);
            }else{
                logger.info("L'utente non ha inserito il nome dell'attività. Impossibile proseguire senza.");
                handleEmptyStoreField();
            }
        });

        dialog.showAndWait();
        logger.info("Dialog per creazione rapida dell'attività configurato e avviato.");
    }

    //NAVIGAZIONE VERSO ALTRE FINESTRE
    //----------------------------------------------------------

    protected void goToLoginView(){
        try{
            Parent root = FXMLLoader.load(Rat23Main.class.getResource("loginView.fxml"));
            Stage window = sourceStage;
            window.setScene(new Scene(root, 1920, 1080));
            window.setResizable(false);
            window.setFullScreen(true);
            ((Stage) resendButton.getScene().getWindow()).close();
        } catch (IOException ioe){
            logger.severe(ioe.getMessage());
            handleGenericExceptions();
        }
    }

    //FEEDBACK E ECCEZIONI
    //----------------------------------------------

    private void signUpCompleteFeedback() {
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Registrazione completata")
                .setMessage("Il tuo account è stato creato correttamente." +
                        "\nEseguire il login per accedere a tutte le funzionalità.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleEmptyCodeField(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Verifica account fallita")
                .setMessage("Inserire prima il codice di verifica inviato per e-mail.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleEmptyStoreField(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Registrazione attività fallita")
                .setMessage("Inserire prima il nome dell'attività.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleOtherAuthExceptions(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Verifica account fallita")
                .setMessage("C'è stato un errore durante la verifica del codice. Riprovare.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleResendCodeException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Qualcosa è andato storto nell'invio del codice. Riprovare.")
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
}
