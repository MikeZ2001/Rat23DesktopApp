package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.Rat23Main;
import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.viewmodel.EmployeeVM;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class EmployeesController implements Initializable {

    @FXML
    private Button createEmployeeButton, deleteEmployeeButton, editEmployeeButton, refreshButton;

    @FXML
    private TableView<Employee> employeesTableView;

    @FXML
    private TableColumn<Employee, String> nameColumn, surnameColumn, roleColumn;

    @FXML
    private TextField searchTextField;

    private EmployeeVM employeeVM;

    private final Logger logger = CustomLogger.getLogger();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        logger.info("Inizializzazione fragment Dipendenti.");
        this.employeeVM = new EmployeeVM();
        employeesTableView.itemsProperty().bind(employeeVM.employeesProperty());
        initializeTableView();
        initializeSearchTextField();
        getEmployees();
        logger.info("Terminata inizializzazione fragment Dipendenti.");
    }

    private void initializeTableView(){
        logger.info("Inizializzazione tabella dipendenti.");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("surname"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("role"));
        logger.info("Terminata inizializzazione tabella dipendenti.");
    }

    private void initializeSearchTextField() {
        logger.info("Inizializzazione text field per ricerca tra dipendenti.");
        searchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (!newValue.equals("") && newValue != null){
                    logger.info("Filtrando i dipendenti per \"" + newValue + "\"");
                    employeesTableView.itemsProperty().bind(employeeVM.filteredEmployeesProperty());
                    employeeVM.filterEmployees(newValue);
                }else{
                    logger.info("Filtri per dipendenti svuotati.");
                    employeesTableView.itemsProperty().bind(employeeVM.employeesProperty());
                }
            }
        });
        logger.info("Terminata inizializzazione text field per ricerca tra dipendenti.");
    }

    @FXML
    private void getEmployees() {
        logger.info("Click su Aggiorna.");
        logger.info("Avvio procedura ottenimento dipendenti lato server.");
        employeeVM.getEmployees();
        logger.info("Terminata procedura ottenimento dipendenti lato server.");
    }

    @FXML
    private void createEmployee(){
        logger.info("Click su Crea.");
        Employee employee = null;
        logger.info("Reindirizzamento al dialog per la creazione di un dipendente.");
        openEditEmployeeDialog(employee);
    }

    @FXML
    private void editEmployee(){
        logger.info("Click su Modifica.");
        Employee selectedEmployee = employeesTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null){
            logger.info("Reindirizzamento al dialog per la modifica di un dipendente.");
            openEditEmployeeDialog(selectedEmployee);
        } else{
            logger.info("L'utente non ha selezionato un dipendente da modificare. Riprovare.");
            handleNoItemSelected();
        }
    }

    @FXML
    private void deleteEmployee(){
        logger.info("Click su Elimina.");
        Employee selectedEmployee = employeesTableView.getSelectionModel().getSelectedItem();

        RAT23Cache cache = RAT23Cache.getCacheInstance();
        if (selectedEmployee.getId() == Long.valueOf(cache.get("currentUserId").toString())) {
            logger.info("L'utente sta tentando di eliminare il proprio account.");
            checkSelfDeletion(selectedEmployee);
        } else{
            if (selectedEmployee != null){
                try {
                    logger.info("Avvio procedura eliminazione account.");
                    employeeVM.deleteEmployee(selectedEmployee);
                    logger.info("Terminata procedura eliminazione account.");
                } catch (UserNotFoundException unfe){
                    logger.severe(unfe.getMessage());
                    handleUserNotFoundException();
                } catch (Exception e){
                    logger.severe(e.getMessage());
                    handleGenericExceptions();
                }
            }
            else{
                logger.info("L'utente non ha selezionato un dipendente da eliminare.");
                handleNoItemSelected();
            }
        }
    }

    //NAVIGAZIONE VERSO ALTRE FINESTRE
    //----------------------------------------------------------
    private void openEditEmployeeDialog(Employee employee){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Rat23Main.class.getResource("editEmployeeDialog.fxml"));
            Parent parent = fxmlLoader.load();
            EditEmployeeController editEmployeeController = fxmlLoader.getController();
            editEmployeeController.initialize(employee, employeeVM);
            Scene scene = new Scene(parent, 600, 300);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();
        }catch (IOException ioe){
            logger.severe(ioe.getMessage());
            handleGenericExceptions();
        }
    }

    private void goToLoginView(){
        try{
            Parent root = FXMLLoader.load(Rat23Main.class.getResource("loginView.fxml"));
            Stage window = (Stage) deleteEmployeeButton.getScene().getWindow();
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
    private void handleGenericExceptions(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Qualcosa è andato storto. Riprovare.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleNoItemSelected(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Selezionare prima un dipendente.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleUserNotFoundException(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Impossibile eliminare l'utente. Account non trovato.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void checkSelfDeletion(Employee selectedEmployee) {
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Attenzione!")
                .setMessage(("Vuoi davvero eliminare il tuo account?\nSarai riportato alla schermata principale nel caso.\n\nATTENZIONE!\nSe questo è l'ultimo account rimasto l'operazione sarà una rimozione totale di tutte le informazioni di questa attività."))
                .setOkButton("Si")
                .setCancelButton("No")
                .build();

        dialog.setOkEventHandler(e -> {
            logger.info("L'utente conferma l'eliminazione del proprio account. Avvio procedura per l'eliminazione dell'account.");
            try {
                employeeVM.selfDelete(selectedEmployee, this::goToLoginView);
                logger.info("Eliminazione account completata.");
            } catch (UserNotFoundException unfe){
                logger.severe(unfe.getMessage());
                    handleUserNotFoundException();
            } catch (Exception ex) {
                logger.severe(ex.getMessage());
                handleGenericExceptions();
            }
        });

        dialog.setCancelEventHandler( e -> {
            logger.info("Click su Annulla.");
            dialog.close();
        });

        dialog.showAndWait();
    }
}
