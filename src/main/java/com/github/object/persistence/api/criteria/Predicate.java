package com.github.object.persistence.api.criteria;

import java.util.List;

public interface Predicate {

    PredicateBuilder builder();

    String asString();

    // TODO: убрать?
    List<String> usedFields();

}
