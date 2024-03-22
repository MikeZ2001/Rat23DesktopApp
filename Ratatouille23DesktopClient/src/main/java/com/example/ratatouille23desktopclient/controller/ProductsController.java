package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.Rat23Main;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Category;
import com.example.ratatouille23desktopclient.model.Product;
import com.example.ratatouille23desktopclient.viewmodel.ProductVM;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ProductsController implements Initializable {

    @FXML
    private Button editProductButton, deleteProductButton, createProductButton, refreshButton;

    @FXML
    private TableView<Product> productsTableView;
    @FXML
    private TableColumn<Product, String> nameColumn, descriptionColumn;

    @FXML
    private TableColumn<Product, Category> categoryColumn;
    @FXML
    private TableColumn<Product, String> priceColumn;

    @FXML
    private TextField searchTextField;

    private ProductVM productVM;
    private final Logger logger = CustomLogger.getLogger();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        logger.info("Inizializzazione fragment Prodotti.");
        productVM = new ProductVM();
        productsTableView.itemsProperty().bind(productVM.productsProperty());

        intializeTableView();

        initializeSearchTextField();

        getProducts();

        logger.info("Terminata inizializzazione fragment Prodotti.");
    }

    private void initializeSearchTextField() {
        logger.info("Inizializzazione text field per ricerca tra i prodotti.");
        searchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (!newValue.equals("") && newValue != null){
                    logger.info("Filtrando i prodotti per \""+newValue+"\"");
                    productsTableView.itemsProperty().bind(productVM.filteredProductsProperty());
                    productVM.filterProducts(newValue);
                }else{
                    logger.info("Filtri per proditti svuotati.");
                    productsTableView.itemsProperty().bind(productVM.productsProperty());
                }
            }
        });
    }

    private void intializeTableView(){
        logger.info("Inizializzazione tablle dei prodotti.");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<Product, Category>("category"));
        categoryColumn.setCellFactory(new Callback<TableColumn<Product, Category>, TableCell<Product, Category>>() {
            @Override
            public TableCell<Product, Category> call(TableColumn<Product, Category> productCategoryTableColumn) {
                return new TableCell<>(){
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
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("description"));
        priceColumn.setCellValueFactory(productPriceCellData -> new SimpleStringProperty(String.valueOf(productPriceCellData.getValue().getPrice() + "€")));

        logger.info("Terminata inizializzazione tablle dei prodotti.");
    }

    @FXML
    private void getProducts() {
        logger.info("Click su Aggiorna.");
        logger.info("Avvio procedura ottenimento prodotti lato server.");
        productVM.getProducts();
        logger.info("Terminata procedura ottenimento prodotti lato server.");
    }

    @FXML
    private void createProduct(){
        logger.info("Click su Crea.");
        Product product = null;
        logger.info("Reindirizzamento al dialog per la creazione di un prodotto.");
        openEditProdutDialog(product);
    }

    @FXML
    private void editProduct(){
        logger.info("Click su Modifica.");
        Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null){
            logger.info("Reindirizzamento al dialog per la modifica del prodotto.");
            openEditProdutDialog(selectedProduct);
        } else{
            logger.info("L'utente non ha selezionato un prodotto da modificare.");
            handleNoItemSelected();
        }
    }

    @FXML
    private void deleteProduct(){
        logger.info("Click su Elimina.");
        Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null){
            logger.info("Avvio procedura eliminazione del prodotto.");
            productVM.deleteProduct(selectedProduct);
            logger.info("Terminata procedura eliminazione del prodotto.");
        } else{
            logger.info("L'utente non ha selezionato un prodotto da eliminare.");
            handleNoItemSelected();
        }
    }

    //NAVIGAZIONE VERSO ALTRE FINESTRE
    //----------------------------------------------------------
    private void openEditProdutDialog(Product product){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Rat23Main.class.getResource("editProductDialog.fxml"));
            Parent parent = fxmlLoader.load();
            EditProductController editProductController = fxmlLoader.getController();
            editProductController.initialize(product, productVM);
            Scene scene = new Scene(parent, 600, 427);
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
                .setMessage("Selezionare prima un prodotto.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
