package com.github.object.persistence.sql.impl;

import com.github.object.persistence.api.criteria.Query;
import com.github.object.persistence.api.session.Session;
import com.github.object.persistence.common.DataSourceWrapper;
import com.github.object.persistence.common.EntityCash;
import com.github.object.persistence.sql.impl.criteria.QueryImpl;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

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
        return mapper.get(connection, entityClass, id);
    }

    public <T> List<T> getRecords(Class<T> entityClass, String predicate) {
        return mapper.get(connection, entityClass, predicate);
    }

    @Override
    public <T> boolean saveOrUpdate(T entity) {
        if (mapper.isEntityExistInDB(connection, entity)) {
            return mapper.update(connection, entity);
        }
        return mapper.insert(connection, entity);
    }

    @Override
    public <T> boolean saveOrUpdate(Collection<T> entities) {
        return mapper.insert(connection, entities);
    }

    @Override
    public <T> void deleteRecord(T entity) {
        mapper.delete(connection, entity);
    }

    public <T> void deleteRecord(Class<T> entityClass, String predicate) {
        mapper.delete(connection, entityClass, predicate);
    }

    @Override
    public <T> Query<T> buildQuery(Class<T> clazz) {
        return QueryImpl.getQuery(EntityCash.getEntityInfo(clazz));
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
