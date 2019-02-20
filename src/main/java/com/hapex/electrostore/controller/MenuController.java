package com.hapex.electrostore.controller;

import com.hapex.electrostore.util.ui.DialogFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Created by barthap on 2019-02-18.
 * No idea what to write here
 * *you know, no IDEA, IntelliJ IDEA xDDD
 */
public class MenuController extends NestedController {

    @FXML void onAboutClick(ActionEvent actionEvent) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("About");
        dialog.setHeaderText("Electronic Storehouse");
        dialog.setContentText("Some content info about\n\nhttps://github.com/barthap");
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    @FXML
    void onAppQuitClick(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Electronic Storehouse");
        confirmAlert.setHeaderText("Definitely?");
        confirmAlert.setContentText("Are you sure you wanna exit app?");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK)
            Platform.exit();
    }

    @FXML
    void onOpenLocations(ActionEvent event) {
        DialogFactory.openModalWindow("/layout/locationsWindow.fxml", "Manage locations", mainController);
    }

    @FXML
    void onOpenPreferences(ActionEvent event) {

    }
}
