package com.hapex.electrostore.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Categories")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private Set<Category> subcategories;

    @OneToMany(mappedBy = "category")
    private Collection<Item> items;

    public void addSubcategory(Category subcategory) {
        subcategory.setParent(this);
        this.subcategories.add(subcategory);
    }

    public void removeSubcategory(Category subcategory) {
        this.subcategories.remove(subcategory);
        subcategory.setParent(null);
    }

    public boolean hasChildren() {
        return !getSubcategories().isEmpty();
    }

    public boolean hasItems() {
        return !getItems().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return id == (category.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
