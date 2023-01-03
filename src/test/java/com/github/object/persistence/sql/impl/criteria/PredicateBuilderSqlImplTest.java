package com.github.object.persistence.sql.impl.criteria;

import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.PredicateBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Тестовые сценарии проверки класса {@link PredicateBuilderSqlImpl}.
 */
class PredicateBuilderSqlImplTest {

    @Test
    void builder_returnBuilderWithDefaultValues() {
        PredicateBuilder builder = PredicateBuilderSqlImpl.builder();

        Assertions.assertTrue(builder.usedVariables().isEmpty());
        Assertions.assertTrue(builder.build().toString().isEmpty());
    }

    @Test
    void and_actualEmpty_returnOnlyOther() {
        PredicateBuilder builder1 = PredicateBuilderSqlImpl.builder();
        PredicateBuilder builder2 = PredicateBuilderSqlImpl.builder()
                .between("var", 0, 10);
        String expectedPredicate = "var BETWEEN 0 AND 10";

        Predicate predicate = builder1.and(builder2).build();

        Assertions.assertEquals(expectedPredicate, predicate.toString());
    }

    @Test
    void test() {
        String expectedPredicate = "(((var1 IS NOT NULL) AND (var2 > 10)) AND (var3 IN (1, 5, 12))) OR" +
                " ((var4 = 123) AND (NOT ((var5 BETWEEN -5 AND 5) OR (var6 <= 0))))";
        Set<String> expectedVariables = Set.of("var1", "var2", "var3", "var4", "var5", "var6");

        Predicate predicate = PredicateBuilderSqlImpl.builder()
                .isNotNull("var1")
                .greaterThan("var2", 10)
                .in("var3", 1, 5, 12)
                .or(PredicateBuilderSqlImpl.builder()
                        .equal("var4", 123)
                        .and(PredicateBuilderSqlImpl.builder()
                                        .between("var5", -5, 5)
                                        .or(PredicateBuilderSqlImpl.builder()
                                                        .lessThanOrEqual("var6", 0)
                                        )
                                        .not()
                        )
                ).build();

        Assertions.assertEquals(expectedVariables, predicate.usedVariables());
        Assertions.assertEquals(expectedPredicate, predicate.toString());
    }
}
