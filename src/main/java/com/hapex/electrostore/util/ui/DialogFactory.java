package com.hapex.electrostore.util.ui;

import com.hapex.electrostore.App;
import com.hapex.electrostore.controller.MainController;
import com.hapex.electrostore.controller.ModalWindowController;
import com.hapex.electrostore.util.FXMLFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by barthap on 2019-02-20.
 */
@Slf4j
public class DialogFactory {
    public static <T extends ModalWindowController> T openModalWindow(String fxmlPath, String windowTitle, MainController mainController) {
        FXMLLoader loader = FXMLFactory.getLoader(fxmlPath);
        Parent dialogRoot;
        try {
            dialogRoot = loader.load();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Stage modal = new Stage(StageStyle.UTILITY);
        modal.initModality(Modality.WINDOW_MODAL);
        modal.initOwner(App.getMainStage());
        Scene scene = new Scene(dialogRoot);
        modal.setScene(scene);
        modal.setTitle(windowTitle);
        modal.show();

        T controller = loader.getController();
        controller.bindParentVariables(modal, mainController);
        return controller;
    }
}
