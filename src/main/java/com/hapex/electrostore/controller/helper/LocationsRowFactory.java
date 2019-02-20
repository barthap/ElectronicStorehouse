package com.hapex.electrostore.controller.helper;

import com.hapex.electrostore.controller.LocationsWindowController;
import com.hapex.electrostore.model.LocationModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.*;
import javafx.util.Callback;

import static com.hapex.electrostore.util.Constants.SERIALIZED_MIME_TYPE;

/**
 * Created by barthap on 2019-02-20.
 */
public class LocationsRowFactory implements Callback<TreeTableView<LocationModel>, TreeTableRow<LocationModel>> {

    private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 2 2 2 2; -fx-padding: 3 3 1 3";

    private TreeTableRow<LocationModel> dropZone;
    private TreeItem<LocationModel> draggedItem;

    private LocationsWindowController controller;

    public LocationsRowFactory(LocationsWindowController controller) {
        this.controller = controller;
    }

    @Override
    public TreeTableRow<LocationModel> call(TreeTableView<LocationModel> treeView) {
        TreeTableRow<LocationModel> row = new TreeTableRow<>();

        row.setOnDragDetected(event -> dragDetected(event, row));
        row.setOnDragOver(event -> dragOver(event, row, treeView));
        row.setOnDragDropped(event -> dragDropped(event, row, treeView));
        row.setOnDragDone(event -> clearDropLocation());

        return row;
    }

    private void dragDetected(MouseEvent event, TreeTableRow<LocationModel> row) {
        if (!row.isEmpty()) {
            draggedItem = row.getTreeItem();

            Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
            db.setDragView(row.snapshot(null, null));
            ClipboardContent cc = new ClipboardContent();
            cc.put(SERIALIZED_MIME_TYPE, row.getIndex());
            db.setContent(cc);
            event.consume();
        }
    }

    private void dragOver(DragEvent event, TreeTableRow<LocationModel> row, TreeTableView<LocationModel> treeView) {
        Dragboard db = event.getDragboard();
        if(draggedItem == null || row.getTreeItem() == draggedItem) return;

        if (acceptable(treeView, db, row)) {
            event.acceptTransferModes(TransferMode.MOVE);
            clearDropLocation();
            dropZone = row;
            row.setStyle(DROP_HINT_STYLE);
            event.consume();
        }
        else
            clearDropLocation();
    }

    private void dragDropped(DragEvent event, TreeTableRow<LocationModel> row, TreeTableView<LocationModel> treeView) {
        Dragboard db = event.getDragboard();
        if (acceptable(treeView, db, row)) {
            TreeItem<LocationModel> thisItem = getTarget(treeView, row);

            draggedItem.getParent().getChildren().remove(draggedItem);
            thisItem.getChildren().add(draggedItem);

            controller.onReorderComplete(draggedItem, thisItem);

            treeView.getSelectionModel().select(draggedItem);
            event.setDropCompleted(true);

            treeView.refresh();
        }
    }

    private boolean acceptable(TreeTableView<LocationModel> treeView, Dragboard db, TreeTableRow<LocationModel> row) {
        boolean result = false;
        if (db.hasContent(SERIALIZED_MIME_TYPE)) {
            int index = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
            if (row.getIndex() != index) {
                TreeItem target = getTarget(treeView, row);
                TreeItem item = treeView.getTreeItem(index);
                result = !isParent(item, target);
            }
        }
        return result;
    }

    private TreeItem<LocationModel> getTarget(TreeTableView<LocationModel> treeView, TreeTableRow<LocationModel> row) {
        TreeItem<LocationModel> target = treeView.getRoot();
        if (!row.isEmpty()) {
            target = row.getTreeItem();
        }
        return target;
    }

    // prevent loops in the tree
    private boolean isParent(TreeItem parent, TreeItem child) {
        boolean result = false;
        while (!result && child != null) {
            result = child.getParent() == parent;
            child = child.getParent();
        }
        return result;
    }

    private void clearDropLocation() {
        if (dropZone != null) dropZone.setStyle("");
    }
}

