package com.github.object.persistence.api.criteria;

import com.github.object.persistence.api.criteria.query.Query;

public interface QueryBuilder<T> {

    QueryBuilder<T> select(Class<T> clazz);

    QueryBuilder<T> update(Class<T> clazz);

    QueryBuilder<T> delete(Class<T> clazz);

    QueryBuilder<T> where(Predicate predicate);

    Query<T> build();

}
