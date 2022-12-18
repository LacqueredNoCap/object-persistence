package com.github.object.persistence;

import com.github.object.persistence.sql.impl.SqlAnnotationParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SqlAnnotationParserTest {
    private final SqlAnnotationParser parser = new SqlAnnotationParser();

    @Test
    void parse_TestEntityClass_createdValidSqlScript(){
        String result = parser.prepareTable(TestEntity.class);
        assertEquals("CREATE TABLE IF NOT EXISTS test_entity (id BIGINT PRIMARY KEY, date DATE);", result);
    }
}