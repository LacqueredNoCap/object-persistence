package com.github.object.persistence.api.criteria;

import java.util.List;

public interface Query<T> {

    List<T> selectWhere(Predicate predicate);

    long updateWhere(Predicate predicate);

    long deleteWhere(Predicate predicate);

}
