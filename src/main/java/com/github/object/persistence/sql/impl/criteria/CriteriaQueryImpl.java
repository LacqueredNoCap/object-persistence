package com.github.object.persistence.sql.impl.criteria;

import java.util.Optional;

import com.github.object.persistence.api.criteria.CriteriaQuery;
import com.github.object.persistence.api.criteria.Predicate;

public class CriteriaQueryImpl<T> implements CriteriaQuery<T> {

    private QueryType queryType;
    private Optional<Predicate> predicate;

    private enum QueryType {

        SELECT("SELECT %s FROM %s"),
        UPDATE("UPDATE %s SET VALUES %s"),
        DELETE("DELETE FROM %s");

        QueryType(String pattern) {}

    }

    private CriteriaQueryImpl() {
        queryType = QueryType.SELECT;
        predicate = Optional.empty();
    }

    public static <T> CriteriaQuery<T> criteriaQuery() {
        return new CriteriaQueryImpl<>();
    }

    @Override
    public CriteriaQuery<T> select(Class<T> clazz) {
        queryType = QueryType.SELECT;
        return null;
    }

    @Override
    public CriteriaQuery<T> update(Class<T> clazz) {
        queryType = QueryType.UPDATE;
        return null;
    }

    @Override
    public CriteriaQuery<T> delete(Class<T> clazz) {
        queryType = QueryType.DELETE;
        return null;
    }

    @Override
    public CriteriaQuery<T> where(Predicate pred) {
        predicate = Optional.ofNullable(pred);
        return null;
    }

}
