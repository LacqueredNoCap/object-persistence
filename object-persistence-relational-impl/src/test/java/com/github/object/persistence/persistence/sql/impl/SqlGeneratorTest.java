package com.github.object.persistence.persistence.sql.impl;

import com.github.object.persistence.api.core.EntityCash;
import com.github.object.persistence.api.core.EntityInfoImpl;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlGeneratorTest {
    private final SqlGenerator generator = SqlGenerator.getInstance();

    @Test
    void insertRecords() {
        try (MockedStatic<EntityCash> mockedStatic = Mockito.mockStatic(EntityCash.class)) {
            mockedStatic.when(() -> EntityCash.getEntityInfo(TestEntity.class)).thenReturn(EntityInfoImpl.create(TestEntity.class));
            String result = generator.insertRecords(
                    TestEntity.class, 3, Set.of("date", "id")
            );
            assertEquals(
                    "INSERT INTO testentity (id, date) VALUES (?, ?), (?, ?), (?, ?);",
                    result
            );
        }
    }

    @Test
    void insertRecord() {
        try (MockedStatic<EntityCash> mockedStatic = Mockito.mockStatic(EntityCash.class)) {
            mockedStatic.when(() -> EntityCash.getEntityInfo(TestEntity.class)).thenReturn(EntityInfoImpl.create(TestEntity.class));
            String result = generator.insertRecord(TestEntity.class, Set.of("date", "id"));
            assertEquals(
                    "INSERT INTO testentity (id, date) VALUES (?, ?);",
                    result
            );
        }
    }

    @Test
    void createTable() {
        try (MockedStatic<EntityCash> mockedStatic = Mockito.mockStatic(EntityCash.class)) {
            mockedStatic.when(() -> EntityCash.getEntityInfo(TestEntity.class)).thenReturn(EntityInfoImpl.create(TestEntity.class));
            String result = generator.createTable(TestEntity.class);
            assertEquals(
                    "CREATE TABLE IF NOT EXISTS testentity (id BIGINT PRIMARY KEY, date DATE);",
                    result
            );
        }
    }

}
