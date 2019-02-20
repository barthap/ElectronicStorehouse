package com.hapex.electrostore.controller.helper;

/**
 * Created by barthap on 2019-02-19.
 */


import com.hapex.electrostore.controller.CategoriesController;
import com.hapex.electrostore.model.CategoryModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.*;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Objects;

import static com.hapex.electrostore.util.Constants.SERIALIZED_MIME_TYPE;

public class CategoriesCellFactory implements Callback<TreeView<CategoryModel>, TreeCell<CategoryModel>> {
    private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 2 2 2 2; -fx-padding: 3 3 1 3";
    private TreeCell<CategoryModel> dropZone;
    private TreeItem<CategoryModel> draggedItem;

    private CategoriesController categoriesController;

    public CategoriesCellFactory(CategoriesController categoriesController) {
        this.categoriesController = categoriesController;
    }


    @Override
    public TreeCell<CategoryModel> call(TreeView<CategoryModel> treeView) {
        TreeCell<CategoryModel> cell = new TextFieldTreeCell<>(new StringConverter<CategoryModel>() {
            @Override
            public String toString(CategoryModel object) {
                return object.getName();
            }

            @Override
            public CategoryModel fromString(String string) {
                CategoryModel temp = categoriesController.getSelectedNode().getValue();
                temp.setName(string);
                return temp;
            }
        });

        cell.setOnDragDetected((MouseEvent event) -> dragDetected(event, cell, treeView));
        cell.setOnDragOver((DragEvent event) -> dragOver(event, cell, treeView));
        cell.setOnDragDropped((DragEvent event) -> drop(event, cell, treeView));
        cell.setOnDragDone((DragEvent event) -> clearDropLocation());

        return cell;
    }

    private void dragDetected(MouseEvent event, TreeCell<CategoryModel> treeCell, TreeView<CategoryModel> treeView) {
        draggedItem = treeCell.getTreeItem();

        // root can't be dragged
        if (draggedItem.getParent() == null) return;
        Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();
        content.put(SERIALIZED_MIME_TYPE, draggedItem.getValue());
        db.setContent(content);
        db.setDragView(treeCell.snapshot(null, null));
        event.consume();
    }

    private void dragOver(DragEvent event, TreeCell<CategoryModel> treeCell, TreeView<CategoryModel> treeView) {
        if (!event.getDragboard().hasContent(SERIALIZED_MIME_TYPE)) return;
        TreeItem<CategoryModel> thisItem = treeCell.getTreeItem();

        // can't drop on itself
        if (draggedItem == null || thisItem == null || thisItem == draggedItem) return;
        // ignore if this is the root
        if (draggedItem.getParent() == null) {
            clearDropLocation();
            return;
        }

        event.acceptTransferModes(TransferMode.MOVE);
        if (!Objects.equals(dropZone, treeCell)) {
            clearDropLocation();
            this.dropZone = treeCell;
            dropZone.setStyle(DROP_HINT_STYLE);
        }
    }

    private void drop(DragEvent event, TreeCell<CategoryModel> treeCell, TreeView<CategoryModel> treeView) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (!db.hasContent(SERIALIZED_MIME_TYPE)) return;

        TreeItem<CategoryModel> thisItem = treeCell.getTreeItem();
        TreeItem<CategoryModel> droppedItemParent = draggedItem.getParent();

        // remove from previous location
        droppedItemParent.getChildren().remove(draggedItem);

        thisItem.getChildren().add(draggedItem);

        categoriesController.onDragDropFinished(draggedItem, thisItem);

        treeView.getSelectionModel().select(draggedItem);
        event.setDropCompleted(success);
    }

    private void clearDropLocation() {
        if (dropZone != null) dropZone.setStyle("");
    }
}
