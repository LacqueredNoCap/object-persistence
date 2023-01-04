package com.github.object.persistence.sql.impl;

import com.github.object.persistence.api.session.Session;
import com.github.object.persistence.common.DataSourceWrapper;
import com.github.object.persistence.common.EntityCash;
import com.github.object.persistence.common.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Collection;

public class SqlSession implements Session {
    private final DataSourceWrapper<Connection> connection;
    private final FromSqlToObjectMapper<Connection> mapper;

    public SqlSession(DataSourceWrapper<Connection> connection, FromSqlToObjectMapper<Connection> mapper) {
        this.connection = connection;
        this.mapper = mapper;
    }


    @Override
    public <T> boolean createTable(Class<T> entityClass) {
        return mapper.createTable(connection, entityClass);
    }

    @Override
    public <T, R> T getRecord(Class<T> entityClass, R id) {
        return (T) mapper.get(connection, entityClass, String.format("%s = %s",
                EntityCash.getEntityInfo(entityClass).getIdField().getName(), id)).get(0);
    }

    public <T> T getRecord(Class<T> entityClass, String predicate) {
        return (T) mapper.get(connection, entityClass, predicate);
    }

    @Override
    public <T> boolean saveOrUpdate(T entity) {
        return mapper.insert(connection, entity);
    }

    @Override
    public <T> boolean saveOrUpdate(Collection<T> entities) {
        return mapper.insert(connection, entities);
    }

    @Override
    public <T> void deleteRecord(T entity) {
        Field idField = EntityCash.getEntityInfo(entity.getClass()).getIdField();
        mapper.delete(connection, entity.getClass(), String.format("%s = %s",
                idField.getName(), ReflectionUtils.getValueFromField(entity, idField)));
    }

    public <T> void deleteRecord(Class<T> entityClass, String predicate) {

    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
