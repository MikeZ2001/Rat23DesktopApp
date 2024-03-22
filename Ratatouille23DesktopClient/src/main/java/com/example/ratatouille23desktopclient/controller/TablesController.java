package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.Rat23Main;
import com.example.ratatouille23desktopclient.caching.RAT23Cache;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Employee;
import com.example.ratatouille23desktopclient.model.OrderItem;
import com.example.ratatouille23desktopclient.model.Table;
import com.example.ratatouille23desktopclient.model.enums.Role;
import com.example.ratatouille23desktopclient.viewmodel.OrderVM;
import com.example.ratatouille23desktopclient.viewmodel.TableVM;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class TablesController implements Initializable {

    @FXML
    private Button createTableButton, editTableButton, deleteTableButton, detailsTableButton, refreshButton;

    @FXML
    private TableView<Table> tablesTableView;
    @FXML
    private TableColumn<Table, String> tablesNameColumn;
    @FXML
    private TableColumn<Table, Integer> tabelsSeatsColumn;

    @FXML
    private TableColumn<Table, Boolean> tablesAvailabilityColumn;

    @FXML
    private TextField searchTextField;

    private TableVM tableVM;

    private final Logger logger = CustomLogger.getLogger();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inizializzazione fragment Tavoli.");
        tableVM = new TableVM();

        tablesTableView.itemsProperty().bind(tableVM.tablesProperty());

        initializeComponents();

        getTables();

        logger.info("Terminata inizializzazione fragment Tavoli.");
    }

    private void initializeComponents(){
        logger.info("Inizializzazione autorizzazioni di ruolo.");
        RAT23Cache<String, String> cache = RAT23Cache.getCacheInstance();
        if (Role.valueOf(cache.get("currentUserRole")).equals(Role.SUPERVISOR)){
            createTableButton.setDisable(true);
            deleteTableButton.setDisable(true);
            editTableButton.setDisable(true);
        }else{
            createTableButton.setDisable(false);
            deleteTableButton.setDisable(false);
            editTableButton.setDisable(false);
        }
        logger.info("Terminata inizializzazione autorizzazioni di ruolo.");

        initializeTableView();
        initializeTableDetailsButton();
        initializeSearchTextField();
    }

    private void initializeSearchTextField() {
        logger.info("Inizializzazione text field per ricerca dei tavoli.");
        searchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (!newValue.equals("") && newValue != null){
                    logger.info("Filtrando i tavoli per \"" + newValue + "\"");
                    tablesTableView.itemsProperty().bind(tableVM.filteredTablesProperty());
                    tableVM.filterTables(newValue);
                }else{
                    logger.info("Filtri per tavoli svuotati.");
                    tablesTableView.itemsProperty().bind(tableVM.tablesProperty());
                }
            }
        });
        logger.info("Terminata inizializzazione text field per ricerca dei tavoli.");
    }

    private void initializeTableView(){
        logger.info("Inizializzazione tabella tavoli.");
        tablesNameColumn.setCellValueFactory(new PropertyValueFactory<Table, String>("name"));
        tablesAvailabilityColumn.setCellFactory(tc -> new TableCell<Table, Boolean>(){
            @Override
            protected void updateItem(Boolean value, boolean empty){
                super.updateItem(value, empty);
                if (value == null || empty){
                    setText(null);
                    setStyle("");
                }else{
                    if (value)
                        setText("SI");
                    else
                        setText("NO");
                }
            }
        });
        tablesAvailabilityColumn.setCellValueFactory(data -> data.getValue().availableProperty());
        tabelsSeatsColumn.setCellValueFactory(new PropertyValueFactory<Table, Integer>("seatsNumber"));

        tablesTableView.setRowFactory(rf -> {
            TableRow<Table> row = new TableRow<>();
            row.setOnMouseClicked( event -> {
                if (event.getClickCount() >= 2 && (! row.isEmpty())){
                    Table selectedTable = row.getItem();
                    logger.info("Doppio click sul tavolo. Reindirizzamento alla schermata dettagli conto del tavolo.");
                    openTableDetails(selectedTable);
                }
            });
            return row;
        });

        logger.info("Inizializzazione tabella tavoli.");
    }

    private void initializeTableDetailsButton(){
        logger.info("Inizializzazione pulsante Dettagli Conto.");
        detailsTableButton.setOnAction(e -> {
            logger.info("Click su Dettagli Conto.");
            Table selectedTable = tablesTableView.getSelectionModel().getSelectedItem();
            if (selectedTable != null && !selectedTable.isAvailable()){
                logger.info("Reindirizzamento alla schermata dettagli conto del tavolo.");
                openTableDetails(selectedTable);
            }
            else if (selectedTable == null){
                handleNoItemSelected();
                logger.info("L'utente non ha selezionato un tavolo di cui aprire i dettagli. Riprovare.");
            }
            else if (selectedTable.isAvailable()){
                handleNoCheckOfTable(selectedTable);
                logger.info("Il tavolo selezionato dall'utente non ha un conto aperto. Inutile aprire la schermata dettagli conto.");
            }
        });
        logger.info("Terminata inizializzazione pulsante Dettagli Conto.");
    }

    @FXML
    private void getTables(){
        logger.info("Click su Aggiorna.");
        logger.info("Avvio procedura ottenimento tavoli lato server.");
        tableVM.getTables();
        logger.info("Terminata procedura ottenimento tavoli lato server.");
    }

    @FXML
    private void createTable(){
        logger.info("Click su Crea.");
        Table table = null;
        logger.info("Reindirizzamento al dialog per la creazione di un tavolo.");
        openEditTableDialog(table);
    }

    @FXML
    private void editTable(){
        logger.info("Click su Modifica.");
        Table selectedTable = tablesTableView.getSelectionModel().getSelectedItem();
        if (selectedTable != null){
            logger.info("Reindirizzamento al dialog per la modifica di un tavolo.");
            openEditTableDialog(selectedTable);
        } else{
            logger.info("L'utente non ha selezionato alcun tavolo da modificare.");
            handleNoItemSelected();
        }
    }

    @FXML
    private void deleteTable(){
        logger.info("Click su Elimina.");
        Table selectedTable = tablesTableView.getSelectionModel().getSelectedItem();
        if (selectedTable != null){
            logger.info("Avvio procedura eliminazione tavolo lato server.");
            tableVM.deleteTable(selectedTable);
            logger.info("Terminata procedura eliminazione tavolo lato server.");
        }
        else{
            handleNoItemSelected();
            logger.info("L'utente non ha selezionato un tavolo da eliminare.");
        }

    }


    //NAVIGAZIONE VERSO ALTRE FINESTRE
    //----------------------------------------------------------
    private void openTableDetails(Table table){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Rat23Main.class.getResource("tableDetailsDialog.fxml"));
            Parent parent = fxmlLoader.load();
            TableDetailsController tableDetailsController = fxmlLoader.getController();
            tableDetailsController.initialize(table);
            Scene scene = new Scene(parent, 1280, 720);
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

    private void openEditTableDialog(Table table){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Rat23Main.class.getResource("editTableDialog.fxml"));
            Parent parent = fxmlLoader.load();
            EditTableController editTableController = fxmlLoader.getController();
            editTableController.initialize(table, tableVM);
            Scene scene = new Scene(parent, 600, 200);
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
                .setMessage("Selezionare prima un tavolo.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleNoCheckOfTable(Table selectedTable) {
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Nessun conto aperto")
                .setMessage("Il tavolo " + selectedTable.getName() + " non ha alcun conto aperto.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
