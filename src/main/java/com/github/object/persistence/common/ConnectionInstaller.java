package com.github.object.persistence.common;

/**
 * установщик соединения
 * @param <T> тип датасурса
 */
public interface ConnectionInstaller<T> {

    DataSourceWrapper<T> installConnection();
}
