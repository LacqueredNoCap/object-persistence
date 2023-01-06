package com.github.object.persistence.api.session;

/**
 * Общий интерфейс для фабрики сессий.
 */
public interface SessionFactory {

    /**
     * Создание сессии с подключением к datasource.
     *
     * @return сессия подключения
     */
    Session openSession();

    Session getCurrentSession();

    void initializeDatasource();
}
