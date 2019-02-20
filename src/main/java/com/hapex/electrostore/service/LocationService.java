package com.hapex.electrostore.service;

import com.hapex.electrostore.dao.LocationDao;
import com.hapex.electrostore.model.LocationView;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by barthap on 2019-02-20.
 */
public class LocationService {
    private final LocationDao dao;

    public LocationService(LocationDao dao) {
        this.dao = dao;
    }

    public Collection<LocationView> getSimpleLocations() {
        return dao.findAll().stream().map(LocationView::new).collect(Collectors.toList());
    }

    public void updateDetails(LocationView value) {
    }

    public void updateOrder(LocationView movedItem, LocationView newParent) {
    }
}
