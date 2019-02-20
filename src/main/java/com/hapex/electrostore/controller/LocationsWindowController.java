package com.hapex.electrostore.controller;

import com.hapex.electrostore.controller.helper.LocationsRowFactory;
import com.hapex.electrostore.controller.helper.TreeBuilder;
import com.hapex.electrostore.model.LocationModel;
import com.hapex.electrostore.service.LocationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by barthap on 2019-02-19.
 */
public class LocationsWindowController extends ModalWindowController implements Initializable {

    private final LocationService service;

    @FXML private TreeTableView<LocationModel> locationsTree;

    @FXML private TreeTableColumn<LocationModel, String > nameColumn;
    @FXML private TreeTableColumn<LocationModel, String > descColumn;
    @FXML private TreeTableColumn<LocationModel, Number> quantityColumn;

    @FXML private TextField nameField;
    @FXML private TextArea descField;

    private TreeItem<LocationModel> selectedItem;

    public LocationsWindowController(LocationService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<LocationModel> root = TreeBuilder.buildLocationRoot(service);

        nameColumn.setCellValueFactory(param -> param.getValue().getValue().getNameProperty());
        descColumn.setCellValueFactory(param -> param.getValue().getValue().getDescProperty());
        quantityColumn.setCellValueFactory(param -> param.getValue().getValue().getItemCountProperty());

        locationsTree.setRowFactory(new LocationsRowFactory(this));
        locationsTree.setRoot(root);
        locationsTree.setShowRoot(false);

    }


    @FXML
    void onAddClick(ActionEvent event) {
        LocationModel parent = null;
        if(selectedItem != null && !selectedItem.equals(locationsTree.getRoot()))
            parent = selectedItem.getValue();

        Optional<DTO> result = showEditDialog(null, parent);
        if(!result.isPresent())
            return;

        LocationModel lv = service.createLocation(result.get().getName(), result.get().getDesc(), parent);

        if(parent != null)
            selectedItem.getChildren().add(new TreeItem<>(lv));
        else
            locationsTree.getRoot().getChildren().add(new TreeItem<>(lv));
    }

    @FXML
    void onCloseClick(ActionEvent event) {
        this.close();
    }

    @FXML
    void onEditClick(ActionEvent event) {
        if(selectedItem == null || selectedItem.equals(locationsTree.getRoot()))
            return;

        SimpleStringProperty name = selectedItem.getValue().getNameProperty();
        SimpleStringProperty desc = selectedItem.getValue().getDescProperty();

        LocationModel parent = selectedItem.getParent().equals(locationsTree.getRoot()) ? null : selectedItem.getParent().getValue();

        Optional<DTO> result = showEditDialog(new DTO(name.get(), desc.get()), parent);
        if(!result.isPresent())
            return;

        name.set(result.get().getName());
        desc.set(result.get().getDesc());

        updateFields();
        service.updateDetails(selectedItem.getValue());
    }

    @FXML
    void onClearClick(ActionEvent event) {
        clearFields();
        clearSelection();
    }

    @FXML
    void onDeleteClick(ActionEvent event) {
        if(selectedItem == null || Objects.equals(selectedItem, locationsTree.getRoot()))
            return;

        if(service.removeLocation(selectedItem.getValue())) {
            selectedItem.getParent().getChildren().remove(selectedItem);
            clearSelection();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot remove location: " + selectedItem.getValue().getNameProperty().get());
            alert.setContentText("Location is not empty! Please remove child locations and items first.");
            alert.showAndWait();
        }

    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        selectedItem = locationsTree.getSelectionModel().getSelectedItem();
        if(selectedItem == null) {
            clearFields();
            return;
        }
        updateFields();
    }

    public void onReorderComplete(TreeItem<LocationModel> movedItem, TreeItem<LocationModel> newParent) {
        service.updateOrder(movedItem.getValue(), newParent.getValue());
    }

    private Optional<DTO> showEditDialog(DTO existingData, LocationModel parent) {
        Dialog<DTO> dialog = new Dialog<>();
        dialog.setTitle(existingData == null ? "Create location" : "Edit location");
        String headerText = "Current parent ";
        headerText += (existingData == null) ? "will be: " : "is: ";
        headerText += parent != null ?  parent.getNameProperty().get() : "root node";
        dialog.setHeaderText(headerText);
        dialog.setResizable(true);

        TextField nameInput = new TextField();
        TextArea descInput = new TextArea();

        descInput.setPrefWidth(400);
        descInput.setPrefHeight(80);

        if(existingData != null) {
            nameInput.setText(existingData.getName());
            descInput.setText(existingData.getDesc());
        }

        GridPane grid = new GridPane();
        grid.setVgap(5);

        grid.add(new Label("Name: "), 1, 1);
        grid.add(nameInput, 2, 1);

        grid.add(new Label("Description: "), 1, 2);
        grid.add(descInput, 2, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return new DTO(nameInput.getText(), descInput.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void clearSelection() {
        locationsTree.getSelectionModel().clearSelection();
        selectedItem = null;
    }

    private void updateFields() {
        LocationModel location = selectedItem.getValue();
        nameField.setText(location.getNameProperty().get());
        descField.setText(location.getDescProperty().get());
    }

    private void clearFields() {
        descField.setText("");
        nameField.setText("");
    }


    @AllArgsConstructor
    @Value
    private class DTO {
        private String name;
        private String desc;
    }
}
