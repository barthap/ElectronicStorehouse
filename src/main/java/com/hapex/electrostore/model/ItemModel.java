package com.hapex.electrostore.model;

import com.hapex.electrostore.entity.Item;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by barthap on 2019-02-20.
 */

@Getter
@ToString
public class ItemModel implements Serializable {
    private Long id;
    private Long categoryId;
    private Long locationId;
    private String locationFullName;

    private SimpleStringProperty nameProperty;
    private SimpleStringProperty descProperty;
    private SimpleIntegerProperty quantityProperty;
    private SimpleStringProperty websiteProperty;

    public ItemModel(Long categoryId, Long locationId, String name, String description, int quantity, String website) {
        this.id = null;
        this.categoryId = categoryId;
        this.locationId = locationId;
        this.nameProperty = new SimpleStringProperty(name);
        this.descProperty = new SimpleStringProperty(description);
        this.quantityProperty = new SimpleIntegerProperty(quantity);
        this.websiteProperty = new SimpleStringProperty(website);
    }

    public ItemModel(Item item) {
        id = item.getId();
        categoryId = item.getCategory().getId();
        if(item.getLocation() != null)
            locationId = item.getLocation().getId();
        else
            locationId = null;

        nameProperty = new SimpleStringProperty(item.getName());
        descProperty = new SimpleStringProperty(item.getDescription());
        quantityProperty = new SimpleIntegerProperty(item.getQuantity());
        websiteProperty = new SimpleStringProperty(item.getWebsite());
    }

}
