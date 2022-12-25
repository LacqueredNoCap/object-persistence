package com.github.object.persistence.api.criteria;

public interface CriteriaQuery<T> {

    CriteriaQuery<T> select(Class<T> clazz);

    CriteriaQuery<T> update(Class<T> clazz);

    CriteriaQuery<T> delete(Class<T> clazz);

    CriteriaQuery<T> where(Predicate predicate);

}
