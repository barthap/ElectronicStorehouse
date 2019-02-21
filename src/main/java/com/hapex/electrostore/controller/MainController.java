package com.hapex.electrostore.controller;

import com.hapex.electrostore.App;
import com.hapex.electrostore.entity.Item;
import com.hapex.electrostore.model.CategoryModel;
import com.hapex.electrostore.model.ItemModel;
import com.hapex.electrostore.util.ui.DialogFactory;
import javafx.event.ActionEvent;
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

    private Item selectedItem;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoriesPaneController.bindMainController(this);
        itemsTablePaneController.bindMainController(this);
        detailsPaneController.bindMainController(this);
        mainMenuController.bindMainController(this);

        leftStatusLabel.setText(App.getLocale("status.ready"));
        rightStatusLabel.setText("---");
    }

    public void setLeftStatus(String text) {
        leftStatusLabel.setText(text);
    }

    public void setRightStatus(String text) {
        rightStatusLabel.setText(text);
    }

    void onSelectedCategoryChanged(CategoryModel selected) {
        //label1.setText("Selected " + selected.getNameProperty());
    }

    void onSelectedItemChanged(Item selected) {
        selectedItem = selected;
        detailsPaneController.setItem(selected);
    }


    public void onSearchKeyTyped(KeyEvent keyEvent) {
        itemsTablePaneController.setFilter(searchField.getText());
    }

    public void onAddItemClick(ActionEvent event) {
        EditItemController controller
                = DialogFactory.openModalWindow("/layout/editItem.fxml", App.getLocale("button.add_item"), this);
        controller.setCreationMode();
    }

    public void onEditItemClick(ActionEvent event) {
        if(selectedItem == null)
            return;
        EditItemController controller
                = DialogFactory.openModalWindow("/layout/editItem.fxml", App.getLocale("button.edit_item"), this);
        controller.setCurrentItem(new ItemModel(selectedItem));
    }

    public void onRemoveItemClick(ActionEvent event) {
    }
}
