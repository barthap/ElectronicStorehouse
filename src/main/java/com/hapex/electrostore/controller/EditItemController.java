package com.hapex.electrostore.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by barthap on 2019-02-19.
 */

@Slf4j
public class EditItemController extends ModalWindowController implements Initializable {

    @FXML
    private Label label1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label1.setText("nullxD");
    }

    @FXML
    void onButton1Click(ActionEvent event) {
        close();
    }

    public void setLabel1Text(String text) {
        label1.setText(text);
    }
}
