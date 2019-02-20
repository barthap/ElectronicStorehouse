package com.hapex.electrostore.model;

import com.hapex.electrostore.entity.Location;
import lombok.Getter;

/**
 * Created by barthap on 2019-02-20.
 */
@Getter
public class SimpleLocationModel {
    private Long id;
    private String fullName;

    public SimpleLocationModel(String fullName) {
        this.fullName = fullName;
        this.id = null;
    }

    public SimpleLocationModel(Location location) {
        id = location.getId();
        fullName = location.getFullName();
    }

    @Override
    public String toString() {
        return fullName;
    }
}
