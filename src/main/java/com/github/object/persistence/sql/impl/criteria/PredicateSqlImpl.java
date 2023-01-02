package com.github.object.persistence.sql.impl.criteria;

import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.PredicateBuilder;

import java.util.List;

public class PredicateSqlImpl implements Predicate {

    private final String predicate;
    private final PredicateInfo info;

    PredicateSqlImpl(String predicate, PredicateInfo info) {
        this.predicate = predicate;
        this.info = info;
    }

    @Override
    public PredicateBuilder builder() {
        return new PredicateBuilderSqlImpl(this);
    }

    @Override
    public String asString() {
        return predicate;
    }

    @Override
    public List<String> usedFields() {
        return info.usedFields();
    }

    PredicateInfo getPredicateInfo() {
        return info;
    }
}
