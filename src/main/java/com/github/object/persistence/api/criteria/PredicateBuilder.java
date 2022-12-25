package com.github.object.persistence.api.criteria;

public interface PredicateBuilder {

    PredicateBuilder and(Predicate predicate);

    PredicateBuilder and();

    PredicateBuilder or(Predicate predicate);

    PredicateBuilder or();

    PredicateBuilder not();

    PredicateBuilder equal(String field, Object value);

    PredicateBuilder greaterThan(String field, Object value);

    PredicateBuilder lessThan(String field, Object value);

    PredicateBuilder in(String field, Object... set);

    PredicateBuilder between(String field, Object leftBound, Object rightBound);

}
