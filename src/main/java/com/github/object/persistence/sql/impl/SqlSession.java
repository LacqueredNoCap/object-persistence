package com.github.object.persistence.sql.impl;

import com.github.object.persistence.api.criteria.Query;
import com.github.object.persistence.api.session.Session;
import com.github.object.persistence.common.DataSourceWrapper;

import java.sql.Connection;

public class SqlSession implements Session {
    private final DataSourceWrapper<Connection> connection;

    public SqlSession(DataSourceWrapper<Connection> connection) {
        this.connection = connection;
    }

    @Override
    public <T, R> T getRecord(Class<T> entityClass, R id) {
        return null;
    }

    @Override
    public <T> Object saveOrUpdate(T entity) {
        return null;
    }

    @Override
    public <T> void deleteRecord(T entity) {

    }

    @Override
    public <T> Query<T> buildQuery(Class<T> clazz) {
        return null;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
