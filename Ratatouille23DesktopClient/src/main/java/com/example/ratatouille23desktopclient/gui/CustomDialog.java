package com.example.ratatouille23desktopclient.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class CustomDialog<T> extends Dialog<T> {
    private CustomDialogBuilder<T> builder;
    private DialogPane pane;


    private CustomDialog(CustomDialogBuilder builder){
        this.builder = builder;

        setTitle(builder.title);

        pane = new DialogPane();

        if (builder.content != null)
            pane.setContent(builder.content);

        if (builder.message != null)
            pane.setContentText(builder.message);

        List<ButtonType> buttons = new ArrayList<>();
        if (builder.okButton != null)
            buttons.add(builder.okButton);

        if (builder.cancelButton != null)
            buttons.add(builder.cancelButton);
        if (buttons.size() != 0)
            pane.getButtonTypes().addAll(buttons);

        pane.getStyleClass().add("custom-dialog");
        pane.getStylesheets().add("customDialog.css");

        setDialogPane(pane);
    }

    public void setOkEventHandler(EventHandler<ActionEvent> eventHandler){
        Button okBttn = (Button) pane.lookupButton(builder.okButton);
        okBttn.addEventFilter(ActionEvent.ACTION, eventHandler);
    }

    public void setCancelEventHandler(EventHandler<ActionEvent> eventHandler){
        if (builder.cancelButton != null){
            Button cancelBttn = (Button) pane.lookupButton(builder.cancelButton);
            cancelBttn.addEventFilter(ActionEvent.ACTION, eventHandler);
        }
    }

    public static class CustomDialogBuilder<T>{
        private String title;
        private Node content;
        private ButtonType okButton;
        private ButtonType cancelButton;
        private String message;

        public CustomDialogBuilder(){
            this.okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        }

        public CustomDialogBuilder<T> setTitle(String title){
            this.title = title;
            return this;
        }

        public CustomDialogBuilder<T> setMessage(String message){
            this.message = message;
            return this;
        }


        public CustomDialogBuilder<T> setContent(Node content){
            this.content = content;
            return this;
        }

        public CustomDialogBuilder<T> setOkButton(String okButtonText){
            this.okButton = new ButtonType(okButtonText);
            return this;
        }

        public CustomDialogBuilder<T> setCancelButton(String cancelButtonText){
            this.cancelButton = new ButtonType(cancelButtonText);
            return this;
        }

        public CustomDialog build(){
            return new CustomDialog(this);
        }
    }
}
