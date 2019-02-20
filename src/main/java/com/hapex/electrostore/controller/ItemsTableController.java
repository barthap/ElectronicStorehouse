package com.hapex.electrostore.controller;

import com.hapex.electrostore.entity.Item;
import com.hapex.electrostore.entity.Location;
import com.hapex.electrostore.service.ItemService;
import com.hapex.electrostore.util.di.Inject;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Created by barthap on 2019-02-18.
 */
public class ItemsTableController extends NestedController implements Initializable {

    @Inject
    private ItemService itemService;

    @FXML private TableView<Item> table;
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, Integer> quantityColumn;
    @FXML private TableColumn<Item, String> locationColumn;


    private List<Item> allItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        locationColumn.setCellValueFactory(param -> {
            Location itemLocation = param.getValue().getLocation();
            String val = "N/A";
            if(itemLocation != null)
                val = itemLocation.getFullName();
            return new ReadOnlyObjectWrapper<>(val);
        });

        allItems = itemService.getAllItems();

        showAllItems();
    }

    void setFilter(String filterText) {

        if(filterText.isEmpty()) {
            showAllItems();
            return;
        }
        List<Item> filteredList = allItems.stream()
                .filter(item ->
                    item.getName().toLowerCase().contains(filterText.toLowerCase()))
                .collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(filteredList));
    }

    private void showAllItems() {
        ObservableList<Item> items = FXCollections.observableArrayList(allItems);
        table.setItems(items);
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        Item selected = table.getSelectionModel().getSelectedItem();
        mainController.onSelectedItemChanged(selected);
    }
}
