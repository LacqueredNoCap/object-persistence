package com.github.object.persistence.api.criteria.query;

import java.util.List;
import java.util.Optional;

public interface SelectQuery<T> extends Query<T> {

    @Override
    Optional<T> getResult();

    @Override
    List<T> getResult();
}
