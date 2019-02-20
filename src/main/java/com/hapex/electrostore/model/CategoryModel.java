package com.hapex.electrostore.model;

import com.hapex.electrostore.entity.Category;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by barthap on 2019-02-18.
 */
@Data
public class CategoryModel implements Serializable {
    long id;
    long parentId;
    String name;


    public CategoryModel(String name) {
        this.name = name;
        this.id = -1;
        this.parentId = -1;
    }

    public CategoryModel(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        if(category.getParent() != null)
            this.parentId = category.getParent().getId();
        else
            this.parentId = -1;
    }
}
