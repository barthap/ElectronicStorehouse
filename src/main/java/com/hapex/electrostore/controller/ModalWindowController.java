package com.hapex.electrostore.controller;

import javafx.stage.Stage;

/**
 * Created by barthap on 2019-02-19.
 */
public abstract class ModalWindowController {
    protected Stage parentStage;
    protected MainController mainController;

    public void bindParentVariables(Stage stage, MainController controller) {
        parentStage = stage;
        mainController = controller;
    }

    public void close() {
        parentStage.close();
    }
}
