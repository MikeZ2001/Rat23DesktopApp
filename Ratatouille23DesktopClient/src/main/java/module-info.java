module com.example.ratatouille23desktopclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.services.cognitoidentityprovider;
    requires retrofit2;
    requires com.google.gson;
    requires retrofit2.converter.gson;
    requires okhttp3;
    requires java.sql;
    requires org.hildan.fxgson;
    requires commons.collections;
    requires org.controlsfx.controls;
    requires org.postgresql.jdbc;
    requires javafx.web;
    requires itextpdf;
    requires PDFViewerFX;


    opens com.example.ratatouille23desktopclient to javafx.fxml;
    exports com.example.ratatouille23desktopclient;
    exports com.example.ratatouille23desktopclient.model to gson;
    exports com.example.ratatouille23desktopclient.gui;
    exports com.example.ratatouille23desktopclient.controller;
    opens com.example.ratatouille23desktopclient.controller to javafx.fxml;
    opens com.example.ratatouille23desktopclient.model to javafx.base, com.google.gson;
    exports com.example.ratatouille23desktopclient.model.enums;
}