package com.hapex.electrostore.service;

import com.hapex.electrostore.dao.LocationDao;
import com.hapex.electrostore.entity.Location;
import com.hapex.electrostore.model.LocationModel;
import com.hapex.electrostore.model.SimpleLocationModel;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by barthap on 2019-02-20.
 */
@Slf4j
public class LocationService {
    private final LocationDao dao;

    public LocationService(LocationDao dao) {
        this.dao = dao;
    }

    public Collection<LocationModel> getSimpleLocations() {
        return dao.findAll().stream().map(LocationModel::new).collect(Collectors.toList());
    }

    public List<SimpleLocationModel> getSimpleLocationModels() {
        return dao.findAll().stream().map(SimpleLocationModel::new).collect(Collectors.toList());
    }

    public LocationModel createLocation(String nameFieldText, String descFieldText, LocationModel parentModel) {
        Location parent = (parentModel != null) ? dao.findById(parentModel.getId()).orElse(null) : null;

        dao.beginTransaction();
        Location location = new Location();
        location.setName(nameFieldText);
        location.setDescription(descFieldText);
        location.setParent(parent);
        dao.persist(location);
        if(parent != null) {
            parent.addChild(location);
            dao.update(parent);
        }

        dao.endTransaction();

        return new LocationModel(location);
    }

    public void updateDetails(LocationModel value) {
        dao.beginTransaction();
        Location edited = dao.findById(value.getId()).orElseThrow(() -> new RuntimeException("Couldn't find category to update"));
        edited.setName(value.getNameProperty().get());
        edited.setDescription(value.getDescProperty().get());
        dao.update(edited);
        dao.endTransaction();
    }

    public void updateOrder(LocationModel movedItem, LocationModel updatedParent) {
        dao.beginTransaction();

        Location moved = dao.findById(movedItem.getId()).orElseThrow(
                () -> new RuntimeException("Couldn't find category with id " + movedItem.getId()));

        //remove old parent
        if(moved.getParent() != null) {
            log.trace("Removing child location from " + moved.getParent().getName());
            moved.getParent().removeChild(moved);
        }

        //set new parent
        if(updatedParent.getId() != null) {
            Location newParent = dao.findById(updatedParent.getId()).orElseThrow(
                    () -> new RuntimeException("Invalid parent location id: " + movedItem.getId()));

            newParent.addChild(moved);

            log.trace("Setting parent: " + newParent.getName());
            dao.update(newParent);
        }
        else    //moved to root category
            moved.setParent(null);

        dao.update(moved);
        dao.endTransaction();
    }

    public boolean removeLocation(LocationModel locationModel) {
        dao.beginTransaction();

        Location location = dao.findById(locationModel.getId()).orElseThrow(
                () -> new RuntimeException("Couldn't find location with id: " + locationModel.getId()));

        if(location.hasChildren() || location.hasItems()) {
            dao.endTransaction();
            return false;
        }

        dao.delete(location);
        dao.endTransaction();

        return true;
    }
}
