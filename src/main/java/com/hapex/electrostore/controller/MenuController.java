package com.hapex.electrostore.controller;

import com.hapex.electrostore.App;
import com.hapex.electrostore.util.FXMLFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
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
        FXMLLoader loader = FXMLFactory.getLoader("/layout/locationsWindow.fxml");
        Parent dialogRoot;
        try {
            dialogRoot = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Stage modal = new Stage(StageStyle.UTILITY);
        modal.initModality(Modality.WINDOW_MODAL);
        modal.initOwner(App.getMainStage());
        Scene scene = new Scene(dialogRoot);
        modal.setScene(scene);
        modal.setTitle("Manage locations");
        modal.show();

        LocationsWindowController controller = loader.getController();
        controller.bindParentVariables(modal, mainController);
    }

    @FXML
    void onOpenPreferences(ActionEvent event) {

    }
}
