package com.example.ratatouille23desktopclient.gui;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class AutoFillTextField extends TextField {
    private ContextMenu popup;

    private Callable<Collection<String>> suggestionsProvider;

    private int maxCharsBeforeSuggestion = 4;
    private int maxSuggestions = 10;
    private double secondsDelaySuggestions = 1;

    private Collection<String> searchResults;

    public AutoFillTextField() {
        super();
        popup = new ContextMenu();

        PauseTransition delay = new PauseTransition(Duration.seconds(secondsDelaySuggestions));
        delay.setOnFinished(evt -> {
            if (getText().length() < maxCharsBeforeSuggestion){
                popup.hide();
            }else{
                searchResults = null;
                try {
                    if (suggestionsProvider != null)
                        searchResults = suggestionsProvider.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (searchResults.size() > 0 && searchResults != null){
                    populatePopup(searchResults);
                    if (!popup.isShowing()){
                        popup.show(AutoFillTextField.this, Side.BOTTOM, 0, 0);
                    }
                }else{
                    popup.hide();
                }
            }
        });

        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                delay.playFromStart();
            }
        });

        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                popup.hide();
            }
        });
    }

    private void populatePopup(Collection<String> searchResults) {
        List<CustomMenuItem> items = new ArrayList<CustomMenuItem>();
        int count = Math.min(searchResults.size(), maxSuggestions);
        for (int i = 0; i < count; i++){
            final String result = searchResults.stream().collect(Collectors.toList()).get(i);
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    setText(result);
                    popup.hide();
                }
            });
            items.add(item);
        }
        popup.getItems().clear();
        popup.getItems().addAll(items);
    }

    public void setSuggestionsProvider(Callable<Collection<String>> suggestionsProvider) {
        this.suggestionsProvider = suggestionsProvider;
    }

    public void setMaxCharsBeforeSuggestion(int maxCharsBeforeSuggestion) {
        this.maxCharsBeforeSuggestion = maxCharsBeforeSuggestion;
    }

    public void setMaxSuggestions(int maxSuggestions) {
        this.maxSuggestions = maxSuggestions;
    }

    public void updateSuggestions(Collection<String> suggestions) {
        this.searchResults = suggestions;
        if (searchResults.size() > 0 && !searchResults.equals(null)) {
            populatePopup(searchResults);
            if (!popup.isShowing()) {
                popup.show(AutoFillTextField.this, Side.BOTTOM, 0, 0);
            }
        }
    }

    public void setSecondsDelaySuggestions(double secondsDelaySuggestions) {
        this.secondsDelaySuggestions = secondsDelaySuggestions;
    }
}
