package com.github.object.persistence.sql.impl.criteria;

enum PredicatePattern {

    AND("(%s) AND (%s)"),
    OR("(%s) OR (%s)"),
    NOT("NOT (%s)"),
    IS_NULL("%s IS NULL"),
    IS_NOT_NULL("%s IS NOT NULL"),
    IN("%s IN (%s)"),
    BETWEEN("%s BETWEEN %s AND %s"),
    EQUAL("%s = %s"),
    LESS_THAN("%s < %s"),
    LESS_THAN_OR_EQUAL("%s <= %s"),
    GREATER_THAN("%s > %s"),
    GREATER_THAN_OR_EQUAL("%s >= %s");

    PredicatePattern(String pattern) {}
}
