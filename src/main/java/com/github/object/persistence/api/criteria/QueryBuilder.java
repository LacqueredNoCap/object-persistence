package com.github.object.persistence.api.criteria;

public interface QueryBuilder<T> {

    QueryBuilder<T> select(Class<T> clazz);

    QueryBuilder<T> update(Class<T> clazz);

    QueryBuilder<T> delete(Class<T> clazz);

    QueryBuilder<T> where(Predicate predicate);

    Query<T> build();

}
