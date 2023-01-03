package com.github.object.persistence.sql.impl.criteria;

import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.PredicateBuilder;

import java.util.Set;

public class PredicateSqlImpl implements Predicate {

    private final String predicate;
    private final Set<String> usedVariables;

    PredicateSqlImpl(String predicate, Set<String> usedVariables) {
        this.predicate = predicate;
        this.usedVariables = Set.copyOf(usedVariables);
    }

    @Override
    public PredicateBuilder builder() {
        return new PredicateBuilderSqlImpl(this);
    }

    @Override
    public Set<String> usedVariables() {
        return usedVariables;
    }

    @Override
    public String toString() {
        return predicate;
    }
}
