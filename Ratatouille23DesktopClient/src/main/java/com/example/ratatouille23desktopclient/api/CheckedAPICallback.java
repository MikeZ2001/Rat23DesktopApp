package com.example.ratatouille23desktopclient.api;

import com.example.ratatouille23desktopclient.gui.CustomDialog;
import javafx.application.Platform;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Logger;

public abstract class CheckedAPICallback<T> implements Callback<T> {
    private boolean successfull = false;


    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void onResponse(Call<T> call, Response<T> response){
        if (response.code() >= 400 && response.code() <= 599){
            successfull = false;
            Platform.runLater(() -> {
                try {
                    handleAPIException(response);
                    logger.severe("Codice errore API: " + response.code() + "\nMessaggio di errore: " + response.errorBody());
                } catch (IOException e) {
                    handleGenericExceptions();
                    logger.severe(e.getMessage());
                }
            });
        }
        else
            successfull = true;
    }

    public boolean isSuccessfull() {
        return successfull;
    }

    @Override
    public void onFailure(Call<T> call, Throwable error) {
        logger.severe(error.getMessage());
        if (error instanceof ConnectException)
            Platform.runLater(() -> handleConnectionException());
        else
            Platform.runLater(() -> handleGenericExceptions());
    }

    private void handleAPIException(Response response) throws IOException {
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore [codice: " + response.code() + "]")
                .setMessage("Risposta dal server:\n" + response.errorBody().string())
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

    private void handleConnectionException() {
        CustomDialog<String> dialog = new CustomDialog.CustomDialogBuilder()
                .setTitle("Errore")
                .setMessage("Impossibile contattare il server. Verificare di essere connessi a Internet.")
                .setOkButton("Ok")
                .build();
        dialog.showAndWait();
    }
}
