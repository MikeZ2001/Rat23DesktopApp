package com.example.ratatouille23desktopclient.controller;

import com.example.ratatouille23desktopclient.Rat23Main;
import com.example.ratatouille23desktopclient.db.DBManager;
import com.example.ratatouille23desktopclient.db.ProdottoDAOPSQL;
import com.example.ratatouille23desktopclient.gui.AutoFillTextField;
import com.example.ratatouille23desktopclient.gui.CustomDialog;
import com.example.ratatouille23desktopclient.helpers.CustomLogger;
import com.example.ratatouille23desktopclient.model.Category;
import com.example.ratatouille23desktopclient.model.Product;
import com.example.ratatouille23desktopclient.viewmodel.CategoryVM;
import com.example.ratatouille23desktopclient.viewmodel.ProductVM;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditProductController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField priceTextField;
    @FXML
    private AutoFillTextField nameTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private ChoiceBox<String> allergensChoiceBox;
    @FXML
    private ComboBox<Category> categoriesComboBox;

    @FXML
    private Button cancelButton, saveButton;

    @FXML
    private FlowPane allergensFlowPane;

    private Product product;

    private ProductVM productVM;
    private CategoryVM categoryVM;

    private final Logger logger = CustomLogger.getLogger();

    private List<String> selectedAllergens = new ArrayList<>();

    public void initialize(Product product, ProductVM productVM){

        logger.log(Level.INFO, "Inizializzazione dialog per creazione/modifica prodotto.");
        this.product = product;
        this.productVM = productVM;
        this.categoryVM = new CategoryVM();
        initializeAllergens();
        initializeCategories();
        setPriceTextFieldListener();
        initializeFields();
        logger.log(Level.INFO, "Terminata nizializzazione dialog per creazione/modifica prodotto.");
    }

    private void initializeCategories() {
        logger.info("Inizializzazione lista di categorie.");
        categoriesComboBox.itemsProperty().bind(categoryVM.categoriesProperty());

        categoriesComboBox.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                if (category != null)
                    return category.getName();
                else
                    return "";
            }

            @Override
            public Category fromString(String s) {
                return null;
            }
        });

        logger.info("Avvio procedura ottenimento categorie lato server.");
        categoryVM.getCategories();
        logger.info("Terminata procedura ottenimento categorie lato server.");

        logger.info("Terminata inizializzazione lista di categorie.");
    }

    private void initializeAllergens() {
        logger.info("Inizializzazione lista allergeni.");
        allergensChoiceBox.itemsProperty().bind(productVM.allergensProperty());

        logger.info("Avvio procedura ottenimento allergeni.");
        try {
            productVM.getAllergens();
            logger.info("Terminata procedura ottenimento allergeni.");
        } catch (IOException e) {
            logger.severe(e.getMessage());
            handleGenericExceptions();
        }

        allergensChoiceBox.setOnAction(actionEvent -> {
            String selectedAllergen = allergensChoiceBox.getSelectionModel().getSelectedItem();
            logger.info("Selezionato un allergene.");
            if (selectedAllergen != null){
                try {
                    VBox allergenTagVBox = FXMLLoader.load(Rat23Main.class.getResource("allergenTag.fxml"));
                    Button allergenTag = (Button) allergenTagVBox.getChildren().get(0);
                    allergenTag.setText("x " + selectedAllergen);
                    allergenTag.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            logger.info("Rimosso un allergene.");
                            String removedAllergen = allergenTag.getText().replace("x ","");
                            allergensChoiceBox.itemsProperty().get().add(removedAllergen);
                            allergensChoiceBox.getSelectionModel().clearSelection();
                            allergensFlowPane.getChildren().remove(allergenTag);
                            selectedAllergens.remove(selectedAllergen);
                        }
                    });
                    allergensFlowPane.getChildren().add(allergenTag);
                    productVM.removeAllergen(selectedAllergen);
                    allergensChoiceBox.getSelectionModel().clearSelection();
                    selectedAllergens.add(selectedAllergen);
                } catch (IOException e) {
                    logger.severe(e.getMessage());
                    handleGenericExceptions();
                }
            }
        });
        logger.info("Terminata inizializzazione lista allergeni.");
    }

    private void initializeFields(){
        logger.info("Inizializzazione campi.");
        if (product == null){
            titleLabel.setText("Crea Prodotto");
        }else{
            titleLabel.setText("Modifica Prodotto");
            nameTextField.setText(product.getName());
            descriptionTextArea.setText(product.getDescription());
            priceTextField.setText(String.valueOf(product.getPrice() + "€"));
            categoriesComboBox.getSelectionModel().select(product.getCategory());
            if (product.getAllergens() != null){
                for (String allergen: product.getAllergens()){
                    allergensChoiceBox.getSelectionModel().select(allergen);
                }
            }
        }

        nameTextField.setMaxSuggestions(8);
        nameTextField.setMaxCharsBeforeSuggestion(5);
        nameTextField.setSecondsDelaySuggestions(0.01);
        logger.info("Configurazione procedura di suggerimento prodotti.");
        nameTextField.setSuggestionsProvider(this::getSuggestionsData);
        logger.info("Terminata configurazione procedura di suggerimento prodotti.");
        logger.info("Terminata inizializzazione campi.");
    }

    private SortedSet<String> getSuggestionsData() {
        logger.info("Avvio procedura ottenimento suggerimenti di prodotti.");
        SortedSet<String> data = new TreeSet<>();
        String term = String.valueOf(nameTextField.getText());
        logger.info("Cerca suggerimenti autocompletamento per valore " +term + " in locale.");
        if (!term.isEmpty()){
            try {
                Connection connection = DBManager.getInstance().getConnection();
                ProdottoDAOPSQL daopsql = new ProdottoDAOPSQL(connection);
                for (String p : daopsql.getProductsByName(term)){
                    data.add(p);
                }
                if (data.size() <= 5){
                    logger.info("Avvio procedura richiesta ulterori suggerimenti lato server.");
                    productVM.getSuggestions(term, () -> {
                        logger.info("Terminata procedura richiesta ulterori suggerimenti lato server.");
                        try {
                            data.clear();
                            for (String p : daopsql.getProductsByName(term)){
                                data.add(p);
                            }
                            nameTextField.updateSuggestions(data);
                        } catch (SQLException e) {
                            handleGenericExceptions();
                            logger.severe(e.getMessage());
                        }
                    });
                }
            } catch (SQLException e) {
                handleGenericExceptions();
                logger.severe(e.getMessage());
            }
        }

        return data;
    }

    private void setPriceTextFieldListener() {
        logger.info("Configurazione campo del prezzo.");
        priceTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldString, String newString) {
                if (!newString.contains("€")){
                    priceTextField.setText(newString + "€");
                }else if (!newString.endsWith("€")){
                    newString = newString.replace("€","") + "€";
                    priceTextField.setText(newString);
                }
            }
        });
        logger.info("Terminata configurazione campo del prezzo.");
    }

    @FXML
    private void cancelEdits(){
        logger.info("Click su Annulla. Reindirizzamento alla schermata dei prodotti.");
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void saveEdits(){
        logger.info("Click su Salva.");
        if (nameTextField.getText().isEmpty() || categoriesComboBox.getSelectionModel().getSelectedItem() == null){
            logger.info("L'utente non ha inserito tutti i dati richiesti per il salvataggio delle modifiche.");
            handleEmptyFields();
        } else {
            try {
                String name = String.valueOf(nameTextField.getText());
                String description = String.valueOf(descriptionTextArea.getText());
                Category category = categoriesComboBox.getSelectionModel().getSelectedItem();
                double price = Double.valueOf(priceTextField.getText().substring(0, priceTextField.getText().length()-1).replace(",","."));

                if (product == null)
                    product = new Product();

                product.setName(name);
                product.setDescription(description);
                product.setCategory(category);
                product.setPrice(price);
                product.setAllergens(FXCollections.observableArrayList(selectedAllergens));

                logger.info("Avvio procedura salvataggio delle modifiche lato server.");
                productVM.updateProduct(product, () -> updateSuccessFeedback(), () -> saveSuccessFeedback());

                ((Stage) saveButton.getScene().getWindow()).close();
            } catch (NumberFormatException nfe){
                logger.info("L'utente ha inserito del testo nel campo del prezzo.");
                handleNotANumber();
            } catch (Exception e){
                logger.severe(e.getMessage());
                handleGenericExceptions();
            }

        }
    }

    //FEEDBACK E ECCEZIONI
    //----------------------------------------------
    private void updateSuccessFeedback(){
        logger.info("Terminata procedura salvataggio delle modifiche lato server. Reindirizzamento alla schermata dei prodotti.");
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Modifica completata")
                .setMessage("Il prodotto è stato aggiornato.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void saveSuccessFeedback(){
        logger.info("Terminata procedura salvataggio delle modifiche lato server. Reindirizzamento alla schermata dei prodotti.");
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Creazione completata")
                .setMessage("Il prodotto è stato creato.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }

    private void handleNotANumber(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Attenzione, il prezzo inserito non è un numero accettabile." +
                        "\nUsare il punto (.) per indicare i centesimi.")
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

    private void handleEmptyFields(){
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Inserire prima almeno il nome e la categoria del prodotto.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
