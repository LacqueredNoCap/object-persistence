package com.github.object.persistence.sql.impl.criteria;

import java.util.Optional;

import com.github.object.persistence.api.criteria.QueryBuilder;
import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.query.Query;

public class QueryBuilderImpl<T> implements QueryBuilder<T> {

    private QueryType queryType;
    private Optional<Predicate> predicate;

    private enum QueryType {

        SELECT("SELECT %s FROM %s"),
        UPDATE("UPDATE %s SET VALUES %s"),
        DELETE("DELETE FROM %s");

        QueryType(String pattern) {}

    }

    private QueryBuilderImpl() {
        queryType = QueryType.SELECT;
        predicate = Optional.empty();
    }

    public static <T> QueryBuilder<T> criteriaQuery() {
        return new QueryBuilderImpl<>();
    }

    @Override
    public QueryBuilder<T> select(Class<T> clazz) {
        queryType = QueryType.SELECT;
        return null;
    }

    @Override
    public QueryBuilder<T> update(Class<T> clazz) {
        queryType = QueryType.UPDATE;
        return null;
    }

    @Override
    public QueryBuilder<T> delete(Class<T> clazz) {
        queryType = QueryType.DELETE;
        return null;
    }

    @Override
    public QueryBuilder<T> where(Predicate pred) {
        predicate = Optional.ofNullable(pred);
        return null;
    }

    @Override
    public Query<T> build() {
        return null;
    }

}
