package com.example.adapters.outbound.persistence;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.core.env.Environment;

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

    public static HibernateSessionFactory fromEnvironment(Environment environment) {
        String url = resolveUrl(environment);
        String username = required(environment, "POSTGRES_USER", "DB_USER");
        String password = required(environment, "POSTGRES_PASSWORD", "DB_PASSWORD");

        Configuration configuration = createConfiguration(url, username, password);
        return new HibernateSessionFactory(configuration.buildSessionFactory());
    }

    static HibernateSessionFactory fromProperties(Map<String, String> environment) {
        String url = resolveUrl(environment);
        String username = required(environment, "POSTGRES_USER", "DB_USER");
        String password = required(environment, "POSTGRES_PASSWORD", "DB_PASSWORD");

        Configuration configuration = createConfiguration(url, username, password);
        return new HibernateSessionFactory(configuration.buildSessionFactory());
    }

    private static Configuration createConfiguration(String url, String username, String password) {
        return new Configuration()
                .addAnnotatedClass(ProductEntity.class)
                .addAnnotatedClass(SaleEntity.class)
                .addAnnotatedClass(SaleDetailEntity.class)
                .addAnnotatedClass(SaleDetailId.class)
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("hibernate.show_sql", "false")
                .setProperty("hibernate.format_sql", "false")
                .setProperty("hibernate.jdbc.time_zone", "UTC");
    }

    private static String resolveUrl(Environment environment) {
        String configuredUrl = environment.getProperty("DB_URL");
        if (configuredUrl != null && !configuredUrl.trim().isEmpty()) {
            return configuredUrl;
        }

        String host = environment.getProperty("POSTGRES_HOST", "localhost");
        String port = environment.getProperty("POSTGRES_PORT", "55432");
        String database = environment.getProperty("POSTGRES_DB", "prototipo_ventas");
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }

    private static String resolveUrl(Map<String, String> environment) {
        String configuredUrl = environment.get("DB_URL");
        if (configuredUrl != null && !configuredUrl.trim().isEmpty()) {
            return configuredUrl;
        }

        String host = environment.getOrDefault("POSTGRES_HOST", "localhost");
        String port = environment.getOrDefault("POSTGRES_PORT", "55432");
        String database = environment.getOrDefault("POSTGRES_DB", "prototipo_ventas");
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void close() {
        sessionFactory.close();
    }

    private static String required(Map<String, String> environment, String primaryName, String fallbackName) {
        String value = environment.get(primaryName);
        if (value == null || value.trim().isEmpty()) {
            value = environment.get(fallbackName);
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                    "La variable de entorno " + primaryName + " es obligatoria.");
        }
        return value;
    }

    private static String required(Environment environment, String primaryName, String fallbackName) {
        String value = environment.getProperty(primaryName);
        if (value == null || value.trim().isEmpty()) {
            value = environment.getProperty(fallbackName);
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                    "La variable de entorno " + primaryName + " es obligatoria.");
        }
        return value;
    }
}