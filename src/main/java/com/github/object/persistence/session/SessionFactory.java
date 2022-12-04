package com.github.object.persistence.session;

import javax.sql.DataSource;

public interface SessionFactory {
    /**
     * Создание сессии с подключением к datasource
     *
     * @param dataSource источник данных
     * @return сессия подключения
     */
    Session createSession(DataSource dataSource);
}
