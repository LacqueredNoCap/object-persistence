package com.github.object.persistence.sql.impl.criteria;

import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.Query;
import com.github.object.persistence.api.session.AbstractSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Тестовые сценарии проверки класса {@link QueryImpl}.
 */
class QueryImplTest {

    private Query<?> query;
    private AbstractSession session;
    private Class<?> clazz;

    @BeforeEach
    void setUp() {
        session = Mockito.mock(AbstractSession.class);
        clazz = Class.class;
        query = QueryImpl.getQuery(session, clazz);
    }

    @Test
    void select_predicateIsNotNull() {
        String predicate = "var IS NOT NULL";

        query.selectWhere(PredicateBuilderSqlImpl.builder().isNotNull("var").build());

        Mockito.verify(session, Mockito.only())
                .getRecords(clazz, Optional.of(predicate));
    }

    @Test
    void select_predicateIsEmpty() {
        query.selectWhere(PredicateBuilderSqlImpl.builder().build());

        Mockito.verify(session, Mockito.only())
                .getRecords(clazz, Optional.empty());
    }

    @Test
    void select_predicateIsNull() {
        query.selectWhere(null);

        Mockito.verify(session, Mockito.only())
                .getRecords(clazz, Optional.empty());
    }

    @Test
    void update_predicateIsNotNull() {
        String predicate = "var IS NOT NULL";
        Map<String, Object> fieldValueMap = Map.of("var1", "val1");

        query.updateWhere(fieldValueMap, PredicateBuilderSqlImpl.builder().isNotNull("var").build());

        Mockito.verify(session, Mockito.only())
                .updateRecord(clazz, fieldValueMap, Optional.of(predicate));
    }

    @Test
    void update_predicateIsEmpty() {
        Map<String, Object> fieldValueMap = Map.of("var1", "val1");

        query.updateWhere(fieldValueMap, PredicateBuilderSqlImpl.builder().build());

        Mockito.verify(session, Mockito.only())
                .updateRecord(clazz, fieldValueMap, Optional.empty());
    }

    @Test
    void update_predicateIsNull() {
        Map<String, Object> fieldValueMap = Map.of("var1", "val1");

        query.updateWhere(fieldValueMap, null);

        Mockito.verify(session, Mockito.only())
                .updateRecord(clazz, fieldValueMap, Optional.empty());
    }

    @Test
    void update_fieldValueMapIsNotEmpty() {
        Map<String, Object> filedValueMap = new HashMap<>();
        filedValueMap.put("var1", "val1");
        filedValueMap.put(null, "val2");
        filedValueMap.put("var3", "val3");
        Predicate predicate = PredicateBuilderSqlImpl.builder().isNotNull("var").build();

        query.updateWhere(filedValueMap, predicate);

        Mockito.verify(session, Mockito.only())
                .updateRecord(
                        clazz,
                        Map.of("var1", "val1", "var3", "val3"),
                        Optional.of(predicate.toString())
                );
    }

    @Test
    void update_fieldValueMapIsEmpty() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> query.updateWhere(Map.of(), PredicateBuilderSqlImpl.builder().isNotNull("var").build())
        );

        Assertions.assertEquals(
                "Значение 'fieldValueMap' не должно быть пустым",
                exception.getMessage()
        );
    }

    @Test
    void update_fieldValueMapContainsOnlyNullKey() {
        Map<String, Object> fieldValueMap = new HashMap<>();
        fieldValueMap.put(null, "val");

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> query.updateWhere(fieldValueMap, PredicateBuilderSqlImpl.builder().isNotNull("var").build())
        );

        Assertions.assertEquals(
                "Значение 'fieldValueMap' не должно быть пустым",
                exception.getMessage()
        );
    }

    @Test
    void update_fieldValueMapIsNull() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> query.updateWhere(null, PredicateBuilderSqlImpl.builder().isNotNull("var").build())
        );

        Assertions.assertEquals(
                "Значение 'fieldValueMap' не должно быть null",
                exception.getMessage()
        );
    }

    @Test
    void delete_predicateIsNotNull() {
        String predicate = "var IS NOT NULL";

        query.deleteWhere(PredicateBuilderSqlImpl.builder().isNotNull("var").build());

        Mockito.verify(session, Mockito.only())
                .deleteRecord(clazz, Optional.of(predicate));
    }

    @Test
    void delete_predicateIsEmpty() {
        query.deleteWhere(PredicateBuilderSqlImpl.builder().build());

        Mockito.verify(session, Mockito.only())
                .deleteRecord(clazz, Optional.empty());
    }

    @Test
    void delete_predicateIsNull() {
        query.deleteWhere(null);

        Mockito.verify(session, Mockito.only())
                .deleteRecord(clazz, Optional.empty());
    }

}