package com.github.object.persistence.api.session;

/**
 * установщик соединения
 *
 * @param <T> тип датасурса
 */
public interface ConnectionInstaller<T> {

    DataSourceWrapper<T> installConnection();
}
