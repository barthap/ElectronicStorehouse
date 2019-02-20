package com.hapex.electrostore.controller;

import com.hapex.electrostore.controller.helper.CategoriesCellFactory;
import com.hapex.electrostore.entity.Category;
import com.hapex.electrostore.model.CategoryView;
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
    private TreeView<CategoryView> categoryTree;

    private ContextMenu contextMenu;
    private TreeItem<CategoryView> selectedNode;

    public CategoriesController(CategoryService service) {
        categoryService = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<CategoryView> root = new TreeItem<>(new CategoryView("Categories"));
        buildCategoryTree(root, categoryService.getSimpleCategories(), null);

        root.setExpanded(true);
        buildContextMenu();

        categoryTree.setCellFactory(new CategoriesCellFactory(this));
        categoryTree.setOnEditCommit(this::commitEdit);
        categoryTree.setRoot(root);
    }

    public void onCategorySelected(MouseEvent mouseEvent) {
        TreeItem<CategoryView> selectedItem = categoryTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null)
            return;
        selectedNode = selectedItem;
        mainController.onSelectedCategoryChanged(selectedItem.getValue());
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.SECONDARY)
            return;

        TreeItem<CategoryView> selectedItem = categoryTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            this.selectedNode = selectedItem;
            contextMenu.show(categoryTree, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }

    }

    public void onDragDropFinished(TreeItem<CategoryView> movedItem, TreeItem<CategoryView> newParent) {
        CategoryView movedCat = movedItem.getValue();
        CategoryView parentCat = newParent.getValue();

        log.debug(String.format("New parent for category %s is %s",
                movedCat.getName(),
                parentCat.getName()));

        categoryService.moveCategory(movedCat, parentCat);
    }

    private void createSubcategory() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Add subcategory for " + selectedNode.toString());
        dialog.setHeaderText("Enter category name");

        Optional<String> dialogResult = dialog.showAndWait();

        if (dialogResult.isPresent()) {
            String newName = dialogResult.get();
            //if(newName.isEmpty())
            //   return; //TODO: add empty name error

            CategoryView cv = categoryService.createNewCategory(newName, selectedNode.getValue());
            TreeItem<CategoryView> treeItem = new TreeItem<>(cv);
            selectedNode.getChildren().add(treeItem);
        }
    }

    private void removeSelected() {
        if (selectedNode == categoryTree.getRoot())
            return;

        if (!categoryService.tryRemove(selectedNode.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot remove category: " + selectedNode.getValue().getName());
            alert.setContentText("Category is not empty! Please remove subcategories and items first.");
            alert.showAndWait();
        } else {
            TreeItem<CategoryView> parent = selectedNode.getParent();
            parent.getChildren().remove(selectedNode);
            selectedNode = parent;
            categoryTree.getSelectionModel().select(parent);
            mainController.setLeftStatus("Category removed successfully");
        }
    }

    private void commitEdit(TreeView.EditEvent<CategoryView> event) {
        //CategoryView old = event.getOldValue();
        CategoryView updated = event.getNewValue();
        log.debug(String.format("Updating category id=%d with new name: %s", updated.getId(), updated.getName()));
        categoryService.updateName(updated);

        categoryTree.setEditable(false);
    }

    private void buildContextMenu() {
        MenuItem entryAdd = new MenuItem("Add subcategory");
        entryAdd.setOnAction(ae -> createSubcategory());

        MenuItem entryEdit = new MenuItem("Edit name");
        entryEdit.setOnAction(ae -> {
            categoryTree.setEditable(true);
            categoryTree.edit(selectedNode);
        });

        MenuItem entryRemove = new MenuItem("Remove");
        entryRemove.setOnAction(ae -> removeSelected());

        contextMenu = new ContextMenu(entryAdd, entryEdit, new SeparatorMenuItem(), entryRemove);
        categoryTree.setContextMenu(contextMenu);
    }

    private void buildCategoryTree(TreeItem<CategoryView> root, Collection<CategoryView> categories, CategoryView parent) {
        Set<CategoryView> filtered;
        filtered = categories.stream()
                .filter(category ->
                        (parent  == null) ?
                                category.getParentId() < 0 :
                                category.getParentId() == parent.getId())
                .collect(Collectors.toSet());

        for (CategoryView cat : filtered) {
            TreeItem<CategoryView> catItem = new TreeItem<>(cat);
            buildCategoryTree(catItem, categories, cat);
            root.getChildren().add(catItem);
        }
    }

    public TreeItem<CategoryView> getSelectedNode() {
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
