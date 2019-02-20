package com.hapex.electrostore.util.database;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Slf4j
public class HibernateUtil {

    private static final EntityManagerFactory entityManagerFactory = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        log.debug("Creating Entity Manager factory...");
        return Persistence.createEntityManagerFactory("sqlite_unit");
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void shutdown() {
        log.debug("Closing Entity manager factory...");
        entityManagerFactory.close();
    }
}
