package com.hapex.electrostore.util.ui;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 * Default JavaFX TreeView\<ItemType> cell factory uses ItemType's toString() method to display
 * node names. This class allows to override this behaviour using lambda.
 * Usage:
 * treeView.setCellFactory(new CustomTextCellFactory(item -> "Custom " + item.getName()));
 *
 * @param <T> Type of item managed by tree - TreeView\<T>
 */
public class CustomTextCellFactory<T> implements Callback<TreeView<T>, TreeCell<T>> {

    private final Callback<T, String> stringTCallback;

    public CustomTextCellFactory(Callback<T, String> stringGetter) {
        this.stringTCallback = stringGetter;
    }

    @Override
    public TreeCell<T> call(TreeView<T> param) {
        return new TreeCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null && !empty)
                    setText(stringTCallback.call(item));
            }
        };
    }
}
