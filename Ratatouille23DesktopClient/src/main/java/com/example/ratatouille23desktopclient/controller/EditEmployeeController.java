package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Category;
import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.model.Store;
import com.example.ratatouille23desktopclient.model.enums.Role;
import com.example.ratatouille23desktopclient.viewmodel.EmployeeVM;
import com.example.ratatouille23desktopclient.viewmodel.StoreVM;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

public class EditEmployeeController {

    @FXML
    private Label titleLabel, passwordLabel;

    @FXML
    private TextField nameTextField, surnameTextField, emailTextField, passwordTextField;

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private Button cancelButton, saveButton;

    private ArrayList<Role> roles;

    private Employee employee;
    private EmployeeVM employeeVM;

    private final Logger logger = CustomLogger.getLogger();

    public void initialize(Employee employee, EmployeeVM dipendenteMV){

        logger.info("Inizializzazione dialog di creazione/modifica di un dipendente.");
        this.employeeVM = dipendenteMV;

        initializeRolesComboBox();

        this.employee = employee;

        initializeFields();

        logger.info("Terminata inizializzazione dialog di creazione/modifica di un dipendente.");
    }

    private void initializeFields(){
        logger.info("Inziializzazione dei campi.");
        if (employee == null){
            titleLabel.setText("Crea Dipendente");
            emailTextField.setEditable(true);
            passwordTextField.setVisible(true);
            passwordTextField.setEditable(true);
            passwordLabel.setVisible(true);
        }else{
            titleLabel.setText("Modifica " + employee.getName() + " " + employee.getSurname());
            nameTextField.setText(employee.getName());
            surnameTextField.setText(employee.getSurname());
            emailTextField.setText(employee.getEmail());
            emailTextField.setEditable(false);
            passwordTextField.setVisible(false);
            passwordLabel.setVisible(false);
            passwordTextField.setEditable(false);
            roleComboBox.getSelectionModel().select(employee.getRole());
        }
        logger.info("Terminata inziializzazione dei campi.");
    }

    private void initializeRolesComboBox(){
        logger.info("Inizializzazione della lista di ruoli.");
        roles = new ArrayList<>();
        roles.addAll(Arrays.stream(Role.values()).toList());
        roleComboBox.getItems().setAll(roles);
        roleComboBox.setCellFactory(new Callback<ListView<Role>, ListCell<Role>>() {
            @Override
            public ListCell<Role> call(ListView<Role> categoriaListView) {
                return new ListCell<Role>(){
                    @Override
                    protected void updateItem(Role role, boolean empty){
                        super.updateItem(role, empty);
                        if (empty || role == null) {
                            setText(null);
                        } else {
                            setText(role.toString());
                        }
                    }
                };
            }
        });

        roleComboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Role role, boolean empty){
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                } else {
                    setText(role.toString());
                }
            }
        });
        roleComboBox.getSelectionModel().select(0);

        logger.info("Terminata inizializzazione della lista di ruoli.");
    }

    @FXML
    private void cancelEdits(){
        logger.info("Click su Annulla. Reindirizzamento alla schermata dei dipendenti.");
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void saveEdits(){
        logger.info("Click su Salva.");
        if (nameTextField.getText().isEmpty() || surnameTextField.getText().isEmpty() || roleComboBox.getSelectionModel().getSelectedItem() == null
                || emailTextField.getText().isEmpty() || (passwordTextField.getText().isEmpty() && this.employee == null)){
            logger.info("L'utente non ha inserito tutti i parametri richiesti per il salvataggio delle modifiche.");
            handleEmptyFields();
        } else {
            try {
                String name = String.valueOf(nameTextField.getText());
                String surname = String.valueOf(surnameTextField.getText());
                Role role = roleComboBox.getSelectionModel().getSelectedItem();
                String email = String.valueOf(emailTextField.getText());
                String password = String.valueOf(passwordTextField.getText());

                if (employee == null)
                    employee = new Employee();

                employee.setName(name);
                employee.setSurname(surname);
                employee.setRole(role);
                employee.setEmail(email);


                logger.info("Avvio procedura per il salvataggio delle modifiche.");
                employeeVM.updateEmployee(employee, password,this::updateSuccessFeedback, this::saveSuccessFeedback);

                ((Stage) saveButton.getScene().getWindow()).close();
            } catch (UsernameExistsException uee) {
                logger.warning("L'utente esiste già.");
                handleUserAlreadyExistsException();
            } catch (InvalidPasswordException ipe){
                logger.warning("La password inserita non è valida.");
                handleInvalidPasswordException();
            } catch (InvalidParameterException ipe){
                logger.warning("I parametri inseriti non sono validi.");
                handleInvalidParameterException();
            } catch (Exception e){
                logger.severe(e.getMessage());
                handleOtherAuthExceptions();
            }

        }
    }

    //FEEDBACK E ECCEZIONI
    //----------------------------------------------
    private void updateSuccessFeedback(){
        logger.info("Terminata procedura per il salvataggio delle modifiche. Reindirizzamento alla schermata dei dipendenti.");
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Modifica completata")
                .setMessage("Il dipendente è stato aggiornato.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void saveSuccessFeedback(){
        logger.info("Terminata procedura per il salvataggio delle modifiche. Reindirizzamento alla schermata dei dipendenti.");
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Creazione completata")
                .setMessage("Il dipendente è stato creato.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleEmptyFields(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Inserire prima tutte le informazioni richieste.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleInvalidParameterException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("I valori inseriti non sono validi." +
                        "\nControllare di aver inserito i valori corretti.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleUserAlreadyExistsException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Esiste già un account con questa e-mail.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleInvalidPasswordException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
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
                .setTitle("Errore")
                .setMessage("C'è stato un errore. Riprovare.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
