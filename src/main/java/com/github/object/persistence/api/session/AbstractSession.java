package com.github.object.persistence.api.session;

import com.github.object.persistence.api.criteria.Query;
import com.github.object.persistence.sql.impl.criteria.QueryImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractSession implements Session {

    public abstract <T> List<T> getRecords(Class<T> entityClass, Optional<String> predicate);

    public abstract <T> long updateRecord(Class<T> entityClass, Map<String, Object> fieldValueMap, Optional<String> predicate);

    public abstract <T> long deleteRecord(Class<T> entityClass, Optional<String> predicate);

    @Override
    public <T> Query<T> buildQuery(Class<T> clazz) {
        return QueryImpl.getQuery(this, clazz);
    }

}
