package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.Rat23Main;
import com.example.ratatouille23desktopclient.aws.auth.AuthController;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;

import java.io.IOException;
import java.util.logging.Logger;

public class ResetPasswordDialogController {
    private String email;

    @FXML
    private Label instructionsLabel;

    @FXML
    private TextField verificationCodeTextField, newPasswordTextField, emailTextField;

    @FXML
    private Button confirmButton, codeRequestButton;
    private Stage sourceStage;

    private final Logger logger = CustomLogger.getLogger();

    public void initialize(Stage sourceStage){
        this.sourceStage = sourceStage;

        logger.info("Dialog per il reset della password configurato e avviato.");
    }

    @FXML
    private void requestResetPassword(){
        logger.info("Click su Invia codice.");
        if (emailTextField.getText().isEmpty()){
            handleEmptyEmailField();
            logger.info("L'utente non ha inserito l'e-mail. Impossibile proseguire senza.");
        } else{
            email = String.valueOf(emailTextField.getText());
            try {
                AuthController authController = new AuthController();
                logger.info("Avvio procedura reset della password.");
                authController.resetPassword(email);
                logger.info("Proceudra reset della password terminata.");
            } catch (InvalidParameterException ipe){
                logger.warning(ipe.getMessage());
                handleWrongEmailException();
            } catch (Exception e) {
                logger.severe(e.getMessage());
                handleResetRequestSendingException();
            }
        }
    }

    @FXML
    private void resetPassword(){
        logger.info("Click su Conferma.");
        if (email != null) {
            if (newPasswordTextField.getText().isEmpty() || verificationCodeTextField.getText().isEmpty()){
                handlePasswordCodeEmptyFields();
                logger.info("L'utente non ha inserito tutti i parametri necessari per la conferma dell'account. Impossibile proseguire senza.");
            } else {
                try {
                    String newPassword = String.valueOf(newPasswordTextField.getText());
                    String verificationCode = String.valueOf(verificationCodeTextField.getText());

                    AuthController authController = new AuthController();
                    logger.info("Avvio procedura di conferma per il reset password.");
                    authController.confirmResetPassword(email, verificationCode, newPassword);
                    logger.info("Procedura di conferma per il reset password terminata.");
                    resetPasswordCompleteFeedback();
                    logger.info("Reindirizzamento alla finestra di login.");
                    goToLoginView();
                } catch (InvalidPasswordException ipe) {
                    logger.warning("Password non valida. Riprovare.");
                    handleInvalidPasswordException();
                } catch (InvalidParameterException ipe){
                    logger.warning("Parametri non validi. Riprovare.");
                    handleInvalidParametersException();
                } catch (Exception e){
                    logger.severe(e.getMessage());
                    handleResetResponseSendingException();
                }
            }
        }
    }

    //NAVIGAZIONE VERSO ALTRE FINESTRE
    //----------------------------------------------------------
    private void goToLoginView() {
        try{
            Parent root = FXMLLoader.load(Rat23Main.class.getResource("loginView.fxml"));
            Stage window = sourceStage;
            window.setScene(new Scene(root, 1920, 1080));
            window.setResizable(false);
            window.setFullScreen(true);
            ((Stage) codeRequestButton.getScene().getWindow()).close();
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

    private void handleResetRequestSendingException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Reset password fallito")
                .setMessage("C'è stato un errore durante l'invio della richiesta. Riprovare.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleResetResponseSendingException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Reset password fallito")
                .setMessage("C'è stato un errore durante l'invio della nuova password. Riprovare.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleEmptyEmailField(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Reset password fallito")
                .setMessage("Inserire prima l'e-mail dell'account di cui vuoi resettare la password.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleWrongEmailException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Reset password fallito")
                .setMessage("L'e-mail inserita non è valida. Controllare di aver scritto correttamente l'e-mail.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleInvalidPasswordException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Reset password fallito")
                .setMessage("La password inserita non è valida." +
                        "\nLa password deve essere composta da almeno 8 caratteri, di cui:" +
                        "\n - almeno 1 carattere minuscolo" +
                        "\n - almeno 1 carattere maiuscolo" +
                        "\n - almeno 1 carattere speciale" +
                        "\n   (^ $ * . [ ] { } ( ) ? - \" ! @ # % & / \\ , > < ' : ; | _ ~ ` + = )" +
                        "\n - almeno 1 numero")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleInvalidParametersException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Reset password fallito")
                .setMessage("I valori inseriti non sono validi. Controllare di aver inserito correttamente le informazioni richieste.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handlePasswordCodeEmptyFields() {
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Reset password fallito")
                .setMessage("Inserire prima il codice di verifica inviato per e-mail e la nuova password.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void resetPasswordCompleteFeedback(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Reset password completato")
                .setMessage("Il reset della password è completato." +
                        "\nEseguire il login per accedere a tutte le funzionalità.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
