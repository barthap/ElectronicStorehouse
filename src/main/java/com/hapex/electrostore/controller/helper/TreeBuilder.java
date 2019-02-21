package com.hapex.electrostore.controller.helper;

import com.hapex.electrostore.App;
import com.hapex.electrostore.model.CategoryModel;
import com.hapex.electrostore.model.LocationModel;
import com.hapex.electrostore.service.CategoryService;
import com.hapex.electrostore.service.LocationService;
import javafx.scene.control.TreeItem;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by barthap on 2019-02-20.
 */
public class TreeBuilder {

    /**
     * Creates root TreeItem for category tree, data is gathered using CategoryService
     * TODO: Extract getSimpleCategories() from CategoryService to external interface
     * @param categoryService category provider
     * @return root TreeItem
     */
    public static TreeItem<CategoryModel> buildCategoryRoot(CategoryService categoryService) {
        TreeItem<CategoryModel> root = new TreeItem<>(new CategoryModel(App.getLocale("category.all")));
        buildCategoryTree(root, categoryService.getSimpleCategories(), null);
        root.setExpanded(true);
        return root;
    }

    public static TreeItem<LocationModel> buildLocationRoot(LocationService locationService) {
        TreeItem<LocationModel> root = new TreeItem<>(new LocationModel("Root"));
        buildLocationTree(root, locationService.getSimpleLocations(), null);
        root.setExpanded(true);
        return root;
    }

    private static void buildCategoryTree(TreeItem<CategoryModel> root, Collection<CategoryModel> categories, CategoryModel parent) {
        Set<CategoryModel> filtered;
        filtered = categories.stream()
                .filter(category ->
                        (parent  == null) ?
                                category.getParentId() < 0 :
                                category.getParentId() == parent.getId())
                .collect(Collectors.toSet());

        for (CategoryModel cat : filtered) {
            TreeItem<CategoryModel> catItem = new TreeItem<>(cat);
            buildCategoryTree(catItem, categories, cat);
            root.getChildren().add(catItem);
        }
    }

    private static void buildLocationTree(TreeItem<LocationModel> root, Collection<LocationModel> locations, LocationModel parent) {
        Set<LocationModel> filtered;
        filtered = locations.stream()
                .filter(l ->
                        (parent  == null) ?
                                l.getParentId() == null :
                                Objects.equals(parent.getId(), l.getParentId()))
                .collect(Collectors.toSet());

        for (LocationModel loc : filtered) {
            TreeItem<LocationModel> treeItem = new TreeItem<>(loc);
            buildLocationTree(treeItem, locations, loc);
            root.getChildren().add(treeItem);
        }
    }
}
