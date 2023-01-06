package com.github.object.persistence.sql.impl.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.Query;
import com.github.object.persistence.api.session.AbstractSession;

public final class QueryImpl<T> implements Query<T> {

    // TODO: EntityInfo будет предоставляться из кэша (Session)
    private final AbstractSession session;
    private final Class<T> clazz;

    private QueryImpl(AbstractSession session, Class<T> clazz) {
        this.session = session;
        this.clazz = clazz;
    }

    public static <T> Query<T> getQuery(AbstractSession session, Class<T> clazz) {
        return new QueryImpl<>(session, clazz);
    }

    @Override
    public List<T> selectWhere(Predicate pred) {
        Optional<String> predicate = Optional.ofNullable(pred).map(Object::toString);
        session.getRecords(clazz, predicate);
        return new ArrayList<>();
    }

    @Override
    public long updateWhere(Map<String, Object> fieldValueMap, Predicate pred) {
        Optional<String> predicate = Optional.ofNullable(pred).map(Object::toString);
        return session.updateRecord(clazz, fieldValueMap, predicate);
    }

    @Override
    public long deleteWhere(Predicate pred) {
        Optional<String> predicate = Optional.ofNullable(pred).map(Object::toString);
        return session.deleteRecord(clazz, predicate);
    }

}
