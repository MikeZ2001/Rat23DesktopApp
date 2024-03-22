package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.Rat23Main;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Category;
import com.example.ratatouille23desktopclient.viewmodel.CategoryVM;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class CategoriesController implements Initializable {

    @FXML
    private ListView<Category> categoriesListView;

    @FXML
    private Button createCategoryButton, editCategoryButton, deleteCategoryButton, refreshButton;

    @FXML
    private TextField searchTextField;

    private CategoryVM categoryVM;

    private final Logger logger = CustomLogger.getLogger();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        logger.info("Inizializzazione del fragment Categorie.");
        categoryVM = new CategoryVM();
        categoriesListView.itemsProperty().bind(categoryVM.categoriesProperty());

        initializeListView();

        initializeSearchTextField();

        getCategories();

        logger.info("Terminata inizializzazione del fragment Categorie.");
    }

    private void initializeSearchTextField() {
        logger.info("Inizializzazione text field per la ricerca.");
        searchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (!newValue.equals("") && newValue != null){
                    logger.info("Filtrando categorie per \"" + newValue + "\"");
                    categoriesListView.itemsProperty().bind(categoryVM.filteredCategoriesProperty());
                    categoryVM.filterCategories(newValue);
                }else{
                    logger.info("Filtro su categorie svuotato.");
                    categoriesListView.itemsProperty().bind(categoryVM.categoriesProperty());
                }
            }
        });
        logger.info("Terminata inizializzazione text field per la ricerca.");
    }

    private void initializeListView(){
        logger.info("Inizializzazione lista di categorie.");
        categoriesListView.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> categoriesListView) {
                return new ListCell<Category>(){
                    @Override
                    public void updateItem(Category category, boolean empty) {
                        super.updateItem(category, empty);
                        if (empty || category == null) {
                            setText(null);
                        } else {
                            setText(category.getName());
                        }
                    }
                };
            }
        });
        logger.info("Terminata inizializzazione lista di categorie.");
    }

    @FXML
    private void getCategories() {
        logger.info("Click su Aggiorna.");
        logger.info("Avvio procedura ottenimento categorie lato server.");
        categoryVM.getCategories();
        logger.info("Terminata procedura ottenimento categorie lato server.");
    }

    @FXML
    private void createCategory(){
        logger.info("Click su Crea. Reindirizzamento al dialog per la creazione di una nuova categoria.");
        Category category = null;
        goToEditCategoryDialog(category);
    }

    @FXML
    private void deleteCategory(){
        logger.info("Click su Elimina.");
        Category selectedCategory = categoriesListView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null){
            logger.info("Avvio procedura eliminazione categoria lato server.");
            categoryVM.deleteCategory(selectedCategory);
            logger.info("Terminata procedura eliminazione categoria lato server.");
        } else{
            logger.info("L'utente non ha selezionato alcuna categoria da eliminare.");
            handleNoItemSelected();
        }
    }

    @FXML
    private void editCategory(){
        logger.info("Click su Modifica.");
        Category categoriaSelezionata = categoriesListView.getSelectionModel().getSelectedItem();
        if (categoriaSelezionata != null){
            logger.info("Reindirizzamento al dialog per la modifica della categoria.");
            goToEditCategoryDialog(categoriaSelezionata);
        }
        else{
            logger.info("L'utente non ha selezionato alcuna categoria da modificare.");
            handleNoItemSelected();
        }
    }

    //NAVIGAZIONE VERSO ALTRE FINESTRE
    //----------------------------------------------------------
    private void goToEditCategoryDialog(Category category){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Rat23Main.class.getResource("editCategoryDialog.fxml"));
            Parent parent = fxmlLoader.load();
            EditCategoryController editCategoryController = fxmlLoader.getController();
            editCategoryController.initialize(category, categoryVM);
            Scene scene = new Scene(parent);
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
                .setMessage("Selezionare prima una categoria.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
