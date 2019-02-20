package com.hapex.electrostore;

import com.hapex.electrostore.dao.CategoryDaoImpl;
import com.hapex.electrostore.dao.ItemDaoImpl;
import com.hapex.electrostore.dao.LocationDaoImpl;
import com.hapex.electrostore.service.CategoryService;
import com.hapex.electrostore.service.ItemService;
import com.hapex.electrostore.service.LocationService;
import com.hapex.electrostore.util.di.Bean;
import com.hapex.electrostore.util.di.Service;

/**
 * Created by barthap on 2019-02-19.
 */
@Service
public class BeanDefinitions {

    @Bean
    public CategoryService categoryService() {
        return new CategoryService(new CategoryDaoImpl());
    }

    @Bean
    public ItemService itemService() {
        return new ItemService(new ItemDaoImpl());
    }

    @Bean
    public LocationService locationService() {
        return new LocationService(new LocationDaoImpl());
    }
}
