package com.hapex.electrostore.controller;

import com.hapex.electrostore.App;
import com.hapex.electrostore.controller.helper.TreeBuilder;
import com.hapex.electrostore.model.CategoryModel;
import com.hapex.electrostore.model.ItemModel;
import com.hapex.electrostore.model.SimpleLocationModel;
import com.hapex.electrostore.service.CategoryService;
import com.hapex.electrostore.service.ItemService;
import com.hapex.electrostore.service.LocationService;
import com.hapex.electrostore.util.ui.CustomTextCellFactory;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by barthap on 2019-02-19.
 */

@Slf4j
public class EditItemController extends ModalWindowController implements Initializable {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final LocationService locationService;

    private ItemModel currentItem;

    private SimpleBooleanProperty isEditing = new SimpleBooleanProperty(false);

    @FXML private TreeView<CategoryModel> categorySelectTree;
    @FXML private ComboBox<SimpleLocationModel> locationPicker;
    @FXML private SplitMenuButton saveButton;
    @FXML private TextField nameField;
    @FXML private TextArea descField;
    @FXML private TextField quantityField;
    @FXML private TextField websiteField;

    public EditItemController(ItemService itemService, CategoryService categoryService, LocationService locationService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.locationService = locationService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<CategoryModel> root = TreeBuilder.buildCategoryRoot(categoryService);

        categorySelectTree.setCellFactory(new CustomTextCellFactory<>(CategoryModel::getName));
        categorySelectTree.setShowRoot(false);
        categorySelectTree.setRoot(root);

        this.isEditing.addListener((observable, oldValue, newValue) -> setSaveButtonBehaviour(newValue));

        locationPicker.getItems().add(new SimpleLocationModel(App.getLocale("location.none")));
        locationPicker.getItems().addAll(locationService.getSimpleLocationModels());

        setSaveButtonBehaviour(isEditing.get());

    }

    public void onSaveClick(ActionEvent event) {
        this.close();
    }

    public void onCloseClick(ActionEvent event) {
        this.close();
    }

    public void setCurrentItem(ItemModel itemModel) {
        this.isEditing.set(true);

        this.currentItem = itemModel;

        TreeItem<CategoryModel> category = findCategoryById(categorySelectTree.getRoot(), itemModel.getCategoryId());
        if(category != null)
            categorySelectTree.getSelectionModel().select(category);

        selectLocationById(itemModel.getLocationId());

        nameField.setText(itemModel.getNameProperty().get());
        descField.setText(itemModel.getDescProperty().get());
        quantityField.setText(Integer.toString(itemModel.getQuantityProperty().get()));
        websiteField.setText(itemModel.getWebsiteProperty().get());
    }

    public void setCreationMode() {
        this.isEditing.set(false);
        clear();
    }

    private void setSaveButtonBehaviour(boolean editing) {
        saveButton.getItems().clear();

        if(editing) {
            String saveCloseStr = App.getLocale("button.save_and_close");
            String saveChangesStr = App.getLocale("button.save_changes");

            MenuItem saveAndClose = new MenuItem(saveCloseStr);
            MenuItem saveChanges = new MenuItem(saveChangesStr);
            saveAndClose.setOnAction(e -> saveButton.setText(saveCloseStr));
            saveChanges.setOnAction(e -> saveButton.setText(saveChangesStr));

            saveButton.setText(saveChangesStr);
            saveButton.getItems().addAll(saveAndClose, saveChanges);
        }
        else {
            String createCloseStr = App.getLocale("button.create_and_close");
            String createNewStr = App.getLocale("button.create_and_new");

            MenuItem createAndClose = new MenuItem(createCloseStr);
            MenuItem createAndNew = new MenuItem(createNewStr);
            createAndClose.setOnAction(e -> saveButton.setText(createCloseStr));
            createAndNew.setOnAction(e -> saveButton.setText(createNewStr));

            saveButton.setText(createCloseStr);
            saveButton.getItems().addAll(createAndClose, createAndNew);
        }
    }

    private void clear() {
        nameField.setText("");
        descField.setText("");
        websiteField.setText("");
        quantityField.setText("");
        categorySelectTree.getSelectionModel().clearSelection();
    }

    private TreeItem<CategoryModel> findCategoryById(TreeItem<CategoryModel> root, long id) {
        if(root.isLeaf())
            return root.getValue().getId() == id ? root : null;

        for (TreeItem<CategoryModel> node : root.getChildren()) {
            if(node.isLeaf()) {
                if(node.getValue().getId() == id)
                    return node;
            }
            else {
                TreeItem<CategoryModel> item = findCategoryById(node, id);
                if(item != null)
                    return item;
            }
        }

        return null;
    }

    private void selectLocationById(Long id) {
        for(SimpleLocationModel loc : locationPicker.getItems()) {
            if(Objects.equals(loc.getId(), id)) {
                locationPicker.getSelectionModel().select(loc);
                return;
            }
        }
    }
}
