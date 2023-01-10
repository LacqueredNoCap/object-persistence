package com.github.object.persistence.sql.impl;

import com.github.object.persistence.api.criteria.Query;
import com.github.object.persistence.api.session.AbstractSession;
import com.github.object.persistence.common.DataSourceWrapper;
import com.github.object.persistence.common.QueryImpl;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SqlSession extends AbstractSession {

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
        return mapper.get(connection, entityClass, id);
    }

    public <T> List<T> getRecords(Class<T> entityClass, Optional<String> predicate) {
        return mapper.get(connection, entityClass, predicate);
    }

    @Override
    public <T> boolean saveOrUpdate(T entity) {
        if (mapper.isEntityExistInDB(connection, entity)) {
            return mapper.update(connection, entity) == 1;
        }
        return mapper.insert(connection, entity) == 1;
    }

    @Override
    public <T> boolean saveOrUpdate(Collection<T> entities) {
        return mapper.insert(connection, entities) == entities.size();
    }

    @Override
    public <T> long updateRecord(Class<T> entityClass, Map<String, Object> fieldValueMap, Optional<String> predicate) {
        return mapper.update(connection, entityClass, fieldValueMap, predicate);
    }

    @Override
    public <T> void deleteRecord(T entity) {
        mapper.delete(connection, entity);
    }

    public <T> void deleteRecord(Class<T> entityClass, Optional<String> predicate) {
        mapper.delete(connection, entityClass, predicate);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
