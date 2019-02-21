package com.hapex.electrostore.controller;

import com.hapex.electrostore.App;
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
        dialog.setTitle(App.getLocale("menu.help.about"));
        dialog.setHeaderText("Electronic Storehouse");
        //TODO: custom About content
        dialog.setContentText("TODO: Some content info about\n\nhttps://github.com/barthap");
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    @FXML
    void onAppQuitClick(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Electronic Storehouse");
        confirmAlert.setHeaderText(App.getLocale("message.definitely"));
        confirmAlert.setContentText(App.getLocale("message.confirm_exit"));
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK)
            Platform.exit();
    }

    @FXML
    void onOpenLocations(ActionEvent event) {
        DialogFactory.openModalWindow("/layout/locationsWindow.fxml", App.getLocale("menu.program.locations"), mainController);
    }

    @FXML
    void onOpenPreferences(ActionEvent event) {

    }
}
