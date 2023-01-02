package com.github.object.persistence.sql.impl.criteria;

import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.PredicateBuilder;

public class PredicateBuilderSqlImpl implements PredicateBuilder {

    private static final String DEFAULT_OPERATOR = "AND";

    private String predicate;
    private PredicateInfo info;

    PredicateBuilderSqlImpl() {
        setDefaultValues();
    }

    PredicateBuilderSqlImpl(PredicateSqlImpl pred) {
        this.predicate = pred.asString();
        this.info = pred.getPredicateInfo();
    }

    private void setDefaultValues() {
        predicate = "";
        info = new PredicateInfo();
    }

    public static PredicateBuilder builder() {
        return new PredicateBuilderSqlImpl();
    }

    @Override
    public PredicateBuilder and(PredicateBuilder other) {
        return null;
    }

    @Override
    public PredicateBuilder and() {
        return null;
    }

    @Override
    public PredicateBuilder or(PredicateBuilder predicate) {
        return null;
    }

    @Override
    public PredicateBuilder or() {
        return null;
    }

    @Override
    public PredicateBuilder not(PredicateBuilder predicate) {
        return null;
    }

    @Override
    public PredicateBuilder isNull(String field) {
        return null;
    }

    @Override
    public PredicateBuilder isNotNull(String field) {
        return null;
    }

    @Override
    public PredicateBuilder equal(String field, Object value) {
        return null;
    }

    @Override
    public PredicateBuilder greaterThan(String field, Object value) {
        return null;
    }

    @Override
    public PredicateBuilder greaterThanOrEqual(String field, Object value) {
        return null;
    }

    @Override
    public PredicateBuilder lessThan(String field, Object value) {
        return null;
    }

    @Override
    public PredicateBuilder lessThanOrEqual(String field, Object value) {
        return null;
    }

    @Override
    public PredicateBuilder in(String field, Object... set) {
        return null;
    }

    @Override
    public PredicateBuilder between(String field, Object leftBound, Object rightBound) {
        return null;
    }

    @Override
    public Predicate build() {
        Predicate pred = new PredicateSqlImpl(predicate, info);
        setDefaultValues();
        return pred;
    }

}
