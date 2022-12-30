package com.github.object.persistence.api.session;

/**
 * Интерфейс источника данных.
 */
public interface DataSource {

    DataSource setUrl();

    DataSource setUsername();

    DataSource setPassword();
}
