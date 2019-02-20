package com.hapex.electrostore.controller;

import com.hapex.electrostore.entity.Item;
import com.hapex.electrostore.model.CategoryView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by barthap on 2019-02-18.
 * No idea what to write here
 * *you know, no IDEA, IntelliJ IDEA xDDD
 */
@Slf4j
public class MainController implements Initializable {

    @FXML private Label leftStatusLabel;
    @FXML private Label rightStatusLabel;

    @FXML private TextField searchField;

    //FXML-included controllers
    @FXML private CategoriesController categoriesPaneController;
    @FXML private ItemsTableController itemsTablePaneController;
    @FXML private DetailsPaneController detailsPaneController;
    @FXML private MenuController        mainMenuController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoriesPaneController.bindMainController(this);
        itemsTablePaneController.bindMainController(this);
        detailsPaneController.bindMainController(this);
        mainMenuController.bindMainController(this);

        leftStatusLabel.setText("Ready");
        rightStatusLabel.setText("---");
    }

    public void setLeftStatus(String text) {
        leftStatusLabel.setText(text);
    }

    public void setRightStatus(String text) {
        rightStatusLabel.setText(text);
    }

    void onSelectedCategoryChanged(CategoryView selected) {
        //label1.setText("Selected " + selected.getName());
    }
    void onSelectedItemChanged(Item selected) {
        detailsPaneController.setItem(selected);
    }


    public void onSearchKeyTyped(KeyEvent keyEvent) {
        itemsTablePaneController.setFilter(searchField.getText());
    }

}
