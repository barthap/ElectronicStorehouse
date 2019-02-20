package com.hapex.electrostore.model;

import com.hapex.electrostore.entity.Location;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by barthap on 2019-02-19.
 */
@Getter
public class LocationModel implements Serializable {
    private Long id;
    private Long parentId;

    private SimpleStringProperty nameProperty;
    private SimpleStringProperty descProperty;
    private SimpleIntegerProperty itemCountProperty;

    public LocationModel(String name) {
        this.nameProperty = new SimpleStringProperty(name);
        this.id = null;
        this.parentId = null;
        this.itemCountProperty = null;
    }

    public LocationModel(Location location) {
        id = location.getId();
        parentId = location.getParent() != null ? location.getParent().getId() : null;

        nameProperty = new SimpleStringProperty(location.getName());
        descProperty = new SimpleStringProperty(location.getDescription());
        itemCountProperty = new SimpleIntegerProperty(location.getItemCount());
    }
}
