package com.github.object.persistence.api.criteria;

public interface Predicate {

    Predicate not();

    String asString();
}
