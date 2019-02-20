package com.hapex.electrostore.controller;

/**
 * Created by barthap on 2019-02-18.
 */
abstract class NestedController {
    protected MainController mainController;

    void bindMainController(MainController controller) {
        this.mainController = controller;
    }
}
