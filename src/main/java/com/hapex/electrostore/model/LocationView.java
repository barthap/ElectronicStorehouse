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
public class LocationView implements Serializable {
    private Long id;
    private Long parentId;

    private SimpleStringProperty name;
    private SimpleStringProperty description;
    private SimpleIntegerProperty itemCount;

    public LocationView(String name) {
        this.name = new SimpleStringProperty(name);
        this.id = null;
        this.parentId = null;
        this.itemCount = null;
    }

    public LocationView(Location location) {
        id = location.getId();
        parentId = location.getParent() != null ? location.getParent().getId() : null;

        name = new SimpleStringProperty(location.getName());
        description = new SimpleStringProperty(location.getDescription());
        itemCount = new SimpleIntegerProperty(location.getItemCount());
    }
}
