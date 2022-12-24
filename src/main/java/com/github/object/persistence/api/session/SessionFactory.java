package com.github.object.persistence.api.session;

/**
 * Общий интерфейс для фабрики сессиий
 */
//should be package-private
public interface SessionFactory {
    /**
     * Создание сессии с подключением к datasource.
     *
     * @return сессия подключения
     */
    Session openSession();

    void initializeDatasource();
}