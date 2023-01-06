package com.github.object.persistence.api.criteria;

import java.util.List;
import java.util.Map;

public interface Query<T> {

    List<T> selectWhere(Predicate predicate);

    long updateWhere(Map<String, Object> fieldValueMap, Predicate predicate);

    long deleteWhere(Predicate predicate);

}
