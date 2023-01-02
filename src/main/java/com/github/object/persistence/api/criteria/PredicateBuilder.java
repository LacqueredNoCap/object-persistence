package com.github.object.persistence.api.criteria;

public interface PredicateBuilder {

    PredicateBuilder and(PredicateBuilder other);

    PredicateBuilder and();

    PredicateBuilder or(PredicateBuilder other);

    PredicateBuilder or();

    PredicateBuilder not(PredicateBuilder predicateBuilder);

    PredicateBuilder equal(String field, Object value);

    PredicateBuilder greaterThan(String field, Object value);

    PredicateBuilder greaterThanOrEqual(String field, Object value);

    PredicateBuilder lessThan(String field, Object value);

    PredicateBuilder lessThanOrEqual(String field, Object value);

    PredicateBuilder in(String field, Object... set);

    PredicateBuilder between(String field, Object leftBound, Object rightBound);

    PredicateBuilder isNull(String field);

    PredicateBuilder isNotNull(String field);

    Predicate build();

}
