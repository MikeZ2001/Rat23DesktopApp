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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {
    @FXML
    private TextField emailTextField, givenNameTextField, familyNameTextField;
    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button cancelButton;

    private final Logger logger = CustomLogger.getLogger();

    @FXML
    protected void signUp() {

        logger.info("Click su Registrati.");
        if (emailTextField.getText().isEmpty() || passwordTextField.getText().isEmpty() || givenNameTextField.getText().isEmpty() || familyNameTextField.getText().isEmpty()){
            handleEmptyInputFields();
            logger.info("L'utente non ha inserito tutti i parametri richiesti per la registrazione. Impossibile proseguire.");
        }else{
            String email = String.valueOf(emailTextField.getText());
            String password = String.valueOf(passwordTextField.getText());
            String given_name = String.valueOf(givenNameTextField.getText());
            String family_name = String.valueOf(familyNameTextField.getText());

            if (validSignUpParams(email,password, given_name, family_name)){
                AuthController authController = new AuthController();
                logger.info("Avvio procedura registrazione account.");
                try {
                    authController.signUpAdmin(email, password, given_name, family_name);
                    logger.info("Procedura di registrazione terminata. Richiesta conferma dell'account.");
                    showVerificationCodeDialog(email);
                }catch (UsernameExistsException uee) {
                    logger.warning("Email già utilizzata per un altro account. Riprovare.");
                    handleUserAlreadyExistsException();
                }catch (InvalidPasswordException ipe) {
                    logger.warning("Password non valida. Riprovare.");
                    handleInvalidPasswordException();
                }catch (InvalidParameterException ipe) {
                    logger.warning("Parametri inseriti non validi. Riprovare.");
                    handleInvalidParameterException();
                }catch (Exception e){
                    logger.severe(e.getMessage());
                    handleOtherAuthExceptions();
                }
            }else{
                handleInvalidParameterException();
            }
        }
    }

    private boolean validSignUpParams(String email, String password, String name, String surname) {
        if (email == null || password == null || name == null || surname == null)
            return false;
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty())
            return false;
        //check password policy
        Pattern passwordRegex = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~\\$^+=<>.\"|_~`]).{8,20}$");
        Matcher matcher = passwordRegex.matcher(password);
        if (!matcher.matches())
            return false;
        //check email regex
        Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        matcher = emailRegex.matcher(email);
        if (!matcher.matches())
            return false;
        return true;
    }

    //NAVIGAZIONE VERSO ALTRE FINESTRE
    //----------------------------------------------------------
    private void showVerificationCodeDialog(String email) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Rat23Main.class.getResource("verificationCodeDialog.fxml"));
            Parent parent = fxmlLoader.load();
            VerificationCodeController verificationCodeController = fxmlLoader.getController();
            verificationCodeController.initialize(email, (Stage) cancelButton.getScene().getWindow());

            Scene scene = new Scene(parent, 500, 300);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setTitle("Conferma registrazione");
            stage.showAndWait();
        }catch (IOException ioe){
            logger.severe(ioe.getMessage());
            handleGenericExceptions();
        }
    }

    @FXML
    protected void goToLoginView(){
        try{
            Parent root = FXMLLoader.load(Rat23Main.class.getResource("loginView.fxml"));
            Stage window = (Stage) cancelButton.getScene().getWindow();
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

    private void handleEmptyInputFields(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Registrazione fallita")
                .setMessage("Inserire prima tutte le informazioni richieste.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleInvalidParameterException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Registrazione fallita")
                .setMessage("I valori inseriti non sono validi." +
                        "\nControllare di aver inserito i valori corretti.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleUserAlreadyExistsException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Registrazione fallita")
                .setMessage("L'e-mail inserita è già usata per un altro account.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleInvalidPasswordException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Registrazione fallita")
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

    private void handleOtherAuthExceptions(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Registrazione fallita")
                .setMessage("C'è stato un errore durante la registrazione. Riprovare.")
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
