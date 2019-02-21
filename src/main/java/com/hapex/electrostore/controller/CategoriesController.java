package com.hapex.electrostore.controller;

import com.hapex.electrostore.App;
import com.hapex.electrostore.controller.helper.CategoriesCellFactory;
import com.hapex.electrostore.controller.helper.TreeBuilder;
import com.hapex.electrostore.entity.Category;
import com.hapex.electrostore.model.CategoryModel;
import com.hapex.electrostore.service.CategoryService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by barthap on 2019-02-18.
 */
@Slf4j
public class CategoriesController extends NestedController implements Initializable {

    private final CategoryService categoryService;

    @FXML
    private TreeView<CategoryModel> categoryTree;

    private ContextMenu contextMenu;
    private TreeItem<CategoryModel> selectedNode;

    public CategoriesController(CategoryService service) {
        categoryService = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<CategoryModel> root = TreeBuilder.buildCategoryRoot(categoryService);
        buildContextMenu();

        categoryTree.setCellFactory(new CategoriesCellFactory(this));
        categoryTree.setOnEditCommit(this::commitEdit);
        categoryTree.setRoot(root);
    }

    public void onCategorySelected(MouseEvent mouseEvent) {
        TreeItem<CategoryModel> selectedItem = categoryTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null)
            return;
        selectedNode = selectedItem;
        mainController.onSelectedCategoryChanged(selectedItem.getValue());
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.SECONDARY)
            return;

        TreeItem<CategoryModel> selectedItem = categoryTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            this.selectedNode = selectedItem;
            contextMenu.show(categoryTree, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }

    }

    public void onDragDropFinished(TreeItem<CategoryModel> movedItem, TreeItem<CategoryModel> newParent) {
        CategoryModel movedCat = movedItem.getValue();
        CategoryModel parentCat = newParent.getValue();

        log.debug(String.format("New parent for category %s is %s",
                movedCat.getName(),
                parentCat.getName()));

        categoryService.moveCategory(movedCat, parentCat);
    }

    private void createSubcategory() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Add subcategory for " + selectedNode.toString());
        dialog.setHeaderText(App.getLocale("message.category.enter_name"));

        Optional<String> dialogResult = dialog.showAndWait();

        if (dialogResult.isPresent()) {
            String newName = dialogResult.get();
            //if(newName.isEmpty())
            //   return; //TODO: add empty name error

            CategoryModel cv = categoryService.createNewCategory(newName, selectedNode.getValue());
            TreeItem<CategoryModel> treeItem = new TreeItem<>(cv);
            selectedNode.getChildren().add(treeItem);
        }
    }

    private void removeSelected() {
        if (selectedNode == categoryTree.getRoot())
            return;

        if (!categoryService.tryRemove(selectedNode.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(App.getLocale("error"));
            alert.setHeaderText(App.getLocale("message.error.category.remove") + selectedNode.getValue().getName());
            alert.setContentText(App.getLocale("message.error.category.remove_details"));
            alert.showAndWait();
        } else {
            TreeItem<CategoryModel> parent = selectedNode.getParent();
            parent.getChildren().remove(selectedNode);
            selectedNode = parent;
            categoryTree.getSelectionModel().select(parent);
            mainController.setLeftStatus(App.getLocale("status.category_removed"));
        }
    }

    private void commitEdit(TreeView.EditEvent<CategoryModel> event) {
        //CategoryModel old = event.getOldValue();
        CategoryModel updated = event.getNewValue();
        log.debug(String.format("Updating category id=%d with new name: %s", updated.getId(), updated.getName()));
        categoryService.updateName(updated);

        categoryTree.setEditable(false);
    }

    private void buildContextMenu() {
        MenuItem entryAdd = new MenuItem(App.getLocale("command.add_subcategory"));
        entryAdd.setOnAction(ae -> createSubcategory());

        MenuItem entryEdit = new MenuItem(App.getLocale("command.edit_name"));
        entryEdit.setOnAction(ae -> {
            categoryTree.setEditable(true);
            categoryTree.edit(selectedNode);
        });

        MenuItem entryRemove = new MenuItem(App.getLocale("button.remove"));
        entryRemove.setOnAction(ae -> removeSelected());

        contextMenu = new ContextMenu(entryAdd, entryEdit, new SeparatorMenuItem(), entryRemove);
        categoryTree.setContextMenu(contextMenu);
    }


    public TreeItem<CategoryModel> getSelectedNode() {
        return selectedNode;
    }

    /**
     * It is slow
     * Each call for getSubcategories() executes Hibernate queries, because of lazy loading
     */
    @Deprecated
    private void buildCategoryTree(TreeItem<Category> root, Collection<Category> categories, Category parent) {
        Set<Category> filtered;
        if (parent == null)
            filtered = categories.stream().filter(category -> category.getParent() == null).collect(Collectors.toSet());
        else
            filtered = parent.getSubcategories();

        for (Category cat : filtered) {
            TreeItem<Category> catItem = new TreeItem<>(cat);
            buildCategoryTree(catItem, categories, cat);
            root.getChildren().add(catItem);
        }
    }
}
