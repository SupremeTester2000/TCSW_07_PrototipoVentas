package com.example.adapters.outbound.persistence;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.example.adapters.outbound.persistence.entity.ProductEntity;
import com.example.adapters.outbound.persistence.entity.SaleDetailEntity;
import com.example.adapters.outbound.persistence.entity.SaleDetailId;
import com.example.adapters.outbound.persistence.entity.SaleEntity;

public final class HibernateSessionFactory implements AutoCloseable {

    private final SessionFactory sessionFactory;

    private HibernateSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public static HibernateSessionFactory fromEnvironment() {
        return fromProperties(System.getenv());
    }

    static HibernateSessionFactory fromProperties(Map<String, String> environment) {
        String url = required(environment, "DB_URL");
        String username = required(environment, "DB_USER");
        String password = required(environment, "DB_PASSWORD");

        Configuration configuration = new Configuration()
                .addAnnotatedClass(ProductEntity.class)
                .addAnnotatedClass(SaleEntity.class)
                .addAnnotatedClass(SaleDetailEntity.class)
                .addAnnotatedClass(SaleDetailId.class)
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("hibernate.show_sql", "false")
                .setProperty("hibernate.format_sql", "false")
                .setProperty("hibernate.jdbc.time_zone", "UTC");

        return new HibernateSessionFactory(configuration.buildSessionFactory());
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void close() {
        sessionFactory.close();
    }

    private static String required(Map<String, String> environment, String name) {
        String value = environment.get(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("La variable de entorno " + name + " es obligatoria.");
        }
        return value;
    }
}