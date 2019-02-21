package com.hapex.electrostore.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

/**
 * Created by barthap on 2019-02-19.
 */
@Entity
@Table(name = "Locations")
@Getter
@Setter
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Location parent;

    @OneToMany(mappedBy = "parent")
    private Set<Location> children;

    @OneToMany(mappedBy = "location")
    private Collection<Item> items;

    public void addChild(Location child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void removeChild(Location child) {
        this.children.remove(child);
        child.setParent(null);
    }

    public String getFullName() {
        if(parent != null)
            return parent.getFullName() + " - " + getName();

        return getName();
    }

    public int getItemCount() {
        return getItemCount(true);
    }
    public int getItemCount(boolean total) {
        if(items == null)
            return 0;
        int sum = items.size();
        if(total)
            sum += children.stream().mapToInt(Location::getItemCount).sum();
        return sum;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        return id.equals(location.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
