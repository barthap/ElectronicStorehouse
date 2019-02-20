package com.hapex.electrostore.service;

import com.hapex.electrostore.dao.CategoryDao;
import com.hapex.electrostore.entity.Category;
import com.hapex.electrostore.model.CategoryView;
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

    public Collection<CategoryView> getSimpleCategories() {
        return dao.findAll().stream().map(CategoryView::new).collect(Collectors.toSet());
    }

    public CategoryView createNewCategory(String newName, CategoryView parentView) {
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

        return new CategoryView(category);
    }

    public void updateName(CategoryView updated) {
        dao.beginTransaction();
        Optional<Category> optionalCategory = dao.findById(updated.getId());
        Category edited = optionalCategory.orElseThrow(() -> new RuntimeException("Couldn't find category to update"));
        edited.setName(updated.getName());
        dao.update(edited);
        dao.endTransaction();
    }

    public void moveCategory(CategoryView movedCat, CategoryView parentCat) {
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

    public boolean tryRemove(CategoryView categoryView) {
        dao.beginTransaction();

        Category category = dao.findById(categoryView.getId()).orElseThrow(
                () -> new RuntimeException("Couldn't find category with id: " + categoryView.getId()));

        if(category.hasChildren() || category.hasItems()) {
            dao.endTransaction();
            return false;
        }

        dao.delete(category);
        dao.endTransaction();

        return true;
    }
}
