package com.github.object.persistence.sql.impl.criteria;

import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.PredicateBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PredicateBuilderSqlImpl implements PredicateBuilder {

    private static final PredicatePattern DEFAULT_OPERATOR = PredicatePattern.AND;

    private String predicate;
    private Set<String> usedVariables;

    PredicateBuilderSqlImpl() {
        setDefaultValues();
    }

    PredicateBuilderSqlImpl(PredicateSqlImpl pred) {
        this.predicate = pred.toString();
        this.usedVariables = new HashSet<>(pred.usedVariables());
    }

    public static PredicateBuilder builder() {
        return new PredicateBuilderSqlImpl();
    }

    @Override
    public PredicateBuilder and(PredicateBuilder other) {
        return addOtherPredicateBuilder(PredicatePattern.AND, other);
    }

    @Override
    public PredicateBuilder or(PredicateBuilder other) {
        return addOtherPredicateBuilder(PredicatePattern.OR, other);
    }

    @Override
    public PredicateBuilder not() {
        this.predicate = PredicatePattern.resolvePattern(
                PredicatePattern.NOT,
                this
        );
        return this;
    }

    @Override
    public PredicateBuilder isNull(String field) {
        usedVariables.add(field);

        String isNull = PredicatePattern.resolvePattern(
                PredicatePattern.IS_NULL,
                field
        );
        buildWithDefaultOperator(isNull);

        return this;
    }

    @Override
    public PredicateBuilder isNotNull(String field) {
        usedVariables.add(field);

        String isNotNull = PredicatePattern.resolvePattern(
                PredicatePattern.IS_NOT_NULL,
                field
        );
        buildWithDefaultOperator(isNotNull);

        return this;
    }

    @Override
    public PredicateBuilder equal(String field, Object value) {
        usedVariables.add(field);

        String equal = PredicatePattern.resolvePattern(
                PredicatePattern.EQUAL,
                field,
                value
        );
        buildWithDefaultOperator(equal);

        return this;
    }

    @Override
    public PredicateBuilder greaterThan(String field, Object value) {
        usedVariables.add(field);

        String greaterThan = PredicatePattern.resolvePattern(
                PredicatePattern.GREATER_THAN,
                field,
                value
        );
        buildWithDefaultOperator(greaterThan);

        return this;
    }

    @Override
    public PredicateBuilder greaterThanOrEqual(String field, Object value) {
        usedVariables.add(field);

        String greaterThanOrEqual = PredicatePattern.resolvePattern(
                PredicatePattern.GREATER_THAN_OR_EQUAL,
                field,
                value
        );
        buildWithDefaultOperator(greaterThanOrEqual);

        return this;
    }

    @Override
    public PredicateBuilder lessThan(String field, Object value) {
        usedVariables.add(field);

        String lessThan = PredicatePattern.resolvePattern(
                PredicatePattern.LESS_THAN,
                field,
                value
        );
        buildWithDefaultOperator(lessThan);

        return this;
    }

    @Override
    public PredicateBuilder lessThanOrEqual(String field, Object value) {
        usedVariables.add(field);

        String lessThanOrEqual = PredicatePattern.resolvePattern(
                PredicatePattern.LESS_THAN_OR_EQUAL,
                field,
                value
        );
        buildWithDefaultOperator(lessThanOrEqual);

        return this;
    }

    @Override
    public PredicateBuilder in(String field, Object... set) {
        usedVariables.add(field);

        String in = PredicatePattern.resolvePattern(
                PredicatePattern.IN,
                field,
                Arrays.toString(set).replaceAll("[\\[\\]]", "")
        );
        buildWithDefaultOperator(in);

        return this;
    }

    @Override
    public PredicateBuilder between(String field, Object leftBound, Object rightBound) {
        usedVariables.add(field);

        String between = PredicatePattern.resolvePattern(
                PredicatePattern.BETWEEN,
                field,
                leftBound,
                rightBound
        );
        buildWithDefaultOperator(between);

        return this;
    }

    @Override
    public Predicate build() {
        Predicate pred = new PredicateSqlImpl(predicate, usedVariables);
        setDefaultValues();
        return pred;
    }

    @Override
    public Set<String> usedVariables() {
        return Set.copyOf(usedVariables);
    }

    private void setDefaultValues() {
        predicate = "";
        usedVariables = new HashSet<>();
    }

    private boolean isPredicateEmpty() {
        return predicate.isBlank();
    }

    private Object wrapValue(Object value) {
        return "'" + value.toString() + "'";
    }

    private PredicateBuilder addOtherPredicateBuilder(PredicatePattern pattern, PredicateBuilder other) {
        usedVariables.addAll(other.usedVariables());

        if (isPredicateEmpty()) {
            return other;
        }

        this.predicate = PredicatePattern.resolvePattern(
                pattern,
                this,
                other
        );
        return this;
    }

    private PredicateBuilder addOneVariablePredicate(PredicatePattern pattern, Object... values) {
        usedVariables.add(values[0].toString());

        String between = PredicatePattern.resolvePattern(
                pattern,
                values

        );
        buildWithDefaultOperator(between);

        return this;
    }

    private void buildWithDefaultOperator(String predicate) {
        if (isPredicateEmpty()) {
            this.predicate = predicate;
            return;
        }

        this.predicate = PredicatePattern.resolvePattern(
                DEFAULT_OPERATOR,
                this,
                predicate
        );
    }

    @Override
    public String toString() {
        return predicate;
    }

}
