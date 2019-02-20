package com.hapex.electrostore.service;

import com.hapex.electrostore.dao.ItemDao;
import com.hapex.electrostore.entity.Item;

import java.util.List;

/**
 * Created by barthap on 2019-02-18.
 */
public class ItemService {

    private final ItemDao dao;

    public ItemService(ItemDao dao) {
        this.dao = dao;
    }

    public List<Item> getAllItems() {
        return dao.findAll();
    }
}
