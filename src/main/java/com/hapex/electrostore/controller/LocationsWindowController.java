package com.hapex.electrostore.controller;

import com.hapex.electrostore.controller.helper.LocationsRowFactory;
import com.hapex.electrostore.model.LocationView;
import com.hapex.electrostore.service.LocationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by barthap on 2019-02-19.
 */
public class LocationsWindowController extends ModalWindowController implements Initializable {

    private final LocationService service;

    @FXML private TreeTableView<LocationView> locationsTree;

    @FXML private TreeTableColumn<LocationView, String > nameColumn;
    @FXML private TreeTableColumn<LocationView, String > descColumn;
    @FXML private TreeTableColumn<LocationView, Number> quantityColumn;

    @FXML private TextField nameField;
    @FXML private TextArea descField;

    private TreeItem<LocationView> selectedItem;

    public LocationsWindowController(LocationService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<LocationView> root = new TreeItem<>(new LocationView("Root"));
        buildLocationTree(root, service.getSimpleLocations(), null);

        nameColumn.setCellValueFactory(param -> param.getValue().getValue().getName());
        descColumn.setCellValueFactory(param -> param.getValue().getValue().getDescription());
        quantityColumn.setCellValueFactory(param -> param.getValue().getValue().getItemCount());

        locationsTree.setRowFactory(new LocationsRowFactory(this));

        root.setExpanded(true);
        locationsTree.setRoot(root);
        locationsTree.setShowRoot(false);

    }

    @FXML
    void onAddClick(ActionEvent event) {

    }

    @FXML
    void onCloseClick(ActionEvent event) {
        this.close();
    }

    @FXML
    void onSaveClick(ActionEvent event) {
        selectedItem.getValue().getName().set(nameField.getText());
        selectedItem.getValue().getDescription().set(descField.getText());

        service.updateDetails(selectedItem.getValue());
    }

    @FXML
    void onClearClick(ActionEvent event) {
        descField.setText("");
        nameField.setText("");

        locationsTree.getSelectionModel().clearSelection();
        selectedItem = null;
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        selectedItem = locationsTree.getSelectionModel().getSelectedItem();
        LocationView location = selectedItem.getValue();
        nameField.setText(location.getName().get());
        descField.setText(location.getDescription().get());
    }

    public void onReorderComplete(TreeItem<LocationView> movedItem, TreeItem<LocationView> newParent) {
        service.updateOrder(movedItem.getValue(), newParent.getValue());
    }

    private void buildLocationTree(TreeItem<LocationView> root, Collection<LocationView> locations, LocationView parent) {
        Set<LocationView> filtered;
        filtered = locations.stream()
                .filter(l ->
                        (parent  == null) ?
                                l.getParentId() == null :
                                parent.getId().equals(l.getParentId()))
                .collect(Collectors.toSet());

        for (LocationView loc : filtered) {
            TreeItem<LocationView> treeItem = new TreeItem<>(loc);
            buildLocationTree(treeItem, locations, loc);
            root.getChildren().add(treeItem);
        }
    }
}
