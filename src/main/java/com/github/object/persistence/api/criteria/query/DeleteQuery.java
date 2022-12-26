package com.github.object.persistence.api.criteria.query;

public interface DeleteQuery<T> extends Query<T> {

    @Override
    Long getResult();
}
