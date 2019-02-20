package com.hapex.electrostore.service;

import com.hapex.electrostore.dao.CategoryDao;
import com.hapex.electrostore.entity.Category;
import com.hapex.electrostore.model.CategoryModel;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by barthap on 2019-02-18.
 */

@Slf4j
public class CategoryService {
    private final CategoryDao dao;

    public CategoryService(CategoryDao dao) {
        this.dao = dao;
    }

    public Collection<CategoryModel> getSimpleCategories() {
        return dao.findAll().stream().map(CategoryModel::new).collect(Collectors.toSet());
    }

    public CategoryModel createNewCategory(String newName, CategoryModel parentView) {
        Optional<Category> optParent = dao.findById(parentView.getId());
        Category parent = optParent.orElse(null);

        dao.beginTransaction();
        Category category = new Category();
        category.setName(newName);
        category.setParent(parent);
        dao.persist(category);
        log.trace("New category id: " + category.getId());

        if(optParent.isPresent()) {
            parent.addSubcategory(category);
            dao.update(parent);
        }

        dao.endTransaction();

        return new CategoryModel(category);
    }

    public void updateName(CategoryModel updated) {
        dao.beginTransaction();
        Optional<Category> optionalCategory = dao.findById(updated.getId());
        Category edited = optionalCategory.orElseThrow(() -> new RuntimeException("Couldn't find category to update"));
        edited.setName(updated.getName());
        dao.update(edited);
        dao.endTransaction();
    }

    public void moveCategory(CategoryModel movedCat, CategoryModel parentCat) {
        dao.beginTransaction();

        Category moved = dao.findById(movedCat.getId()).orElseThrow(
                () -> new RuntimeException("Couldn't find category with id " + movedCat.getId()));

        //remove old parent
        if(moved.getParent() != null) {
            log.trace("Removing subcategory from " + moved.getParent().getName());
            moved.getParent().removeSubcategory(moved);
        }

        //set new parent
        if(parentCat.getId() > 0) {
            Category newParent = dao.findById(parentCat.getId()).orElseThrow(
                    () -> new RuntimeException("Invalid parent category id: " + movedCat.getId()));

            newParent.addSubcategory(moved);

            log.trace("Setting parent: " + newParent.getName());
            dao.update(newParent);
        }
        else    //moved to root category
            moved.setParent(null);

        dao.update(moved);
        dao.endTransaction();
    }

    public boolean tryRemove(CategoryModel categoryModel) {
        dao.beginTransaction();

        Category category = dao.findById(categoryModel.getId()).orElseThrow(
                () -> new RuntimeException("Couldn't find category with id: " + categoryModel.getId()));

        if(category.hasChildren() || category.hasItems()) {
            dao.endTransaction();
            return false;
        }

        dao.delete(category);
        dao.endTransaction();

        return true;
    }
}
