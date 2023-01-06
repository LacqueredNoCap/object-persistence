package com.github.object.persistence.api.criteria;

import java.util.Set;

public interface Predicate {

    PredicateBuilder builder();

    Set<String> usedVariables();

    boolean isEmpty();

}
