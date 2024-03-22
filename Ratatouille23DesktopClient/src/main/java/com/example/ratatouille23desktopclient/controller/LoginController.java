package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.Rat23Main;
import com.example.ratatouille23desktopclient.aws.auth.AuthController;
import com.example.ratatouille23desktopclient.exceptions.ForcePasswordChangeException;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotConfirmedException;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LoginController {
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Button loginButton, registerButton, exitBttn;

    @FXML
    private Button resetPasswordButton;

    private final Logger logger = CustomLogger.getLogger();

    public void initialize() throws IOException {

        logger.info("Finestra di login inizializzata.");
    }

    @FXML
    protected void login() {
        logger.info("Click su Login.");
        if (emailTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()){
            handleEmptyInputFields();
            logger.info("L'utente non ha fornito tutti i parametri necessari per l'autenticazione. Impossibile proseguire senza.");
        }else{
            AuthController authController = new AuthController();
            String email = String.valueOf(emailTextField.getText());
            String password = String.valueOf(passwordTextField.getText());
            try{
                logger.info("Avvio procedura di autenticazione.");
                authController.login(email, password);
                logger.info("Autenticazione terminata. Reindirizzamento alla schermata principale.");
                goToEsercizioView();
            }catch (ForcePasswordChangeException fpce) {
                logger.info("L'utente autenticato deve prima resettare la password.");
                handleForcePasswordException(authController, email);
            }catch (NotAuthorizedException nae){
                logger.warning(nae.getMessage());
                handleNotAuthorizedException();
            } catch (UserNotConfirmedException uncfe) {
                logger.info("L'utente autenticato deve prima confermare l'account.");
                try {
                    logger.info("Avvio procedura per l'invio del codice di conferma per e-mail.");
                    authController.resendCode(email);
                    logger.info("Procedura per l'invio del codice di conferma per e-mail terminata.");
                    showVerificationCodeDialog(email);
                } catch (Exception e) {
                    logger.severe(e.getMessage());
                    handleOtherAuthExceptions();
                }
            }catch (Exception e) {
                logger.severe(e.getMessage());
                handleOtherAuthExceptions();
            }
        }
    }

    private void forceChangePassword(AuthController authController, String email, String newPassword){
        try {
            logger.info("Avvio procedura di reimpostazione password.");
            authController.forceChangePassword(email, newPassword);
            logger.info("Procedura per la reimpostazione password terminata.");
        }catch (Exception e){
            logger.severe(e.getMessage());
            handleOtherAuthExceptions();
        }
    }


    //NAVIGAZIONE VERSO ALTRE FINESTRE
    //----------------------------------------------------------
    @FXML
    protected void resetPassword(){
        logger.info("Click su Reset Password.");
        showResetPasswordDialog();
    }

    @FXML
    private void goToSignUpView(){
        logger.info("Click su Sign Up");
        try{
            Parent root = FXMLLoader.load(Rat23Main.class.getResource("signUpView.fxml"));
            Stage window = (Stage) registerButton.getScene().getWindow();
            window.setScene(new Scene(root, 1920, 1080));
            window.setResizable(false);
            window.setFullScreen(true);
        } catch (IOException ioe){
            logger.severe(ioe.getMessage());
            handleGenericExceptions();
        }
    }

    private void goToEsercizioView(){
        try{
            Parent root = FXMLLoader.load(Rat23Main.class.getResource("mainWindow.fxml"));
            Stage window = (Stage) loginButton.getScene().getWindow();
            window.setScene(new Scene(root, 1920, 1080));
            window.setResizable(false);
            window.setFullScreen(true);
        } catch (IOException ioe){
            logger.severe(ioe.getMessage());
            handleGenericExceptions();
        }
    }

    private void showResetPasswordDialog(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Rat23Main.class.getResource("resetPasswordDialog.fxml"));
            Parent parent = fxmlLoader.load();
            ResetPasswordDialogController resetPasswordDialogController = fxmlLoader.getController();
            resetPasswordDialogController.initialize((Stage) loginButton.getScene().getWindow());

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setTitle("Cambia password");
            stage.showAndWait();
        }catch (IOException ioe){
            logger.severe(ioe.getMessage());
            handleGenericExceptions();
        }
    }

    private void showVerificationCodeDialog(String email) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Rat23Main.class.getResource("verificationCodeDialog.fxml"));
            Parent parent = fxmlLoader.load();
            VerificationCodeController verificationCodeController = fxmlLoader.getController();
            verificationCodeController.initialize(email, (Stage) loginButton.getScene().getWindow());

            Scene scene = new Scene(parent, 500, 300);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setTitle("Conferma account");
            stage.showAndWait();
        }catch (IOException ioe){
            logger.severe(ioe.getMessage());
            handleGenericExceptions();
        }
    }

    @FXML
    protected void closeApp(){
        logger.info("Click su Esci.");
        Platform.exit();
        System.exit(0);
    }

    //FEEDBACK E ECCEZIONI
    //----------------------------------------------

    private void handleForcePasswordException(AuthController authController, String email){
        VBox content = new VBox();
        Label instructions = new Label("Dato che è il tuo primo accesso, devi fornire una nuova password per l'account.");
        TextField newPasswordTextField = new TextField();
        newPasswordTextField.setPromptText("Nuova password");
        content.getChildren().addAll(instructions, newPasswordTextField);

        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Autenticazione incompleta")
                .setContent(content)
                .setOkButton("Ok")
                .build();

        dialog.setOkEventHandler(okEvent -> {
            logger.info("Click su OK.");
            String newPassword = String.valueOf(newPasswordTextField.getText());
            if (!newPassword.equals("")){
                forceChangePassword(authController, email, newPassword);
            }
        });

        dialog.showAndWait();
        logger.info("Dialog per reimpostazione password configurato e avviato.");
    }

    private void handleNotAuthorizedException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Autenticazione fallita")
                .setMessage("Utente non trovato.\nControllare che le credenziali siano corrette.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleEmptyInputFields(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Autenticazione fallita")
                .setMessage("Inserire prima e-mail e password.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleOtherAuthExceptions(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Autenticazione fallita")
                .setMessage("C'è stato un errore durante l'autenticazione. Riprovare.")
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