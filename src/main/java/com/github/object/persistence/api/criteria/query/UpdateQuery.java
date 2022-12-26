package com.github.object.persistence.api.criteria.query;

public interface UpdateQuery<T> extends Query<T> {

    @Override
    Long getResult();
}
