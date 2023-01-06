package com.github.object.persistence.sql.impl;

import com.github.object.persistence.TestEntity;
import com.github.object.persistence.common.EntityCash;
import com.github.object.persistence.common.EntityInfoImpl;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlGeneratorTest {
    SqlGenerator generator = SqlGenerator.getInstance();
    private static final String ENTITY_NAME = "TestEntity";
//    @Test
//    void insertRecords() {
//        try (MockedStatic<EntityCash> mockedStatic = Mockito.mockStatic(EntityCash.class)) {
//            mockedStatic.when(() -> EntityCash.getEntityInfo(TestEntity.class)).thenReturn(EntityInfoImpl.create(TestEntity.class));
//            LocalDate date = LocalDate.now();
//            String result = generator.insertRecords(
//                    List.of(new TestEntity(1L, date), new TestEntity(2L, date)),
//                    ENTITY_NAME
//            );
//            assertEquals(
//                    String.format("INSERT INTO testEntity (date, id) VALUES ('%s', '1'), ('%s', '2');", date, date),
//                    result
//            );
//        }
//    }
//
//    @Test
//    void insertRecord() {
//        try (MockedStatic<EntityCash> mockedStatic = Mockito.mockStatic(EntityCash.class)) {
//            mockedStatic.when(() -> EntityCash.getEntityInfo(TestEntity.class)).thenReturn(EntityInfoImpl.create(TestEntity.class));
//            LocalDate date = LocalDate.now();
//            String result = generator.insertRecord(new TestEntity(1L, date));
//            assertEquals(
//                    String.format("INSERT INTO testEntity (date, id) VALUES ('%s', '1');", date),
//                    result
//            );
//        }
//    }

    @Test
    void createTable() {
        try (MockedStatic<EntityCash> mockedStatic = Mockito.mockStatic(EntityCash.class)) {
            mockedStatic.when(() -> EntityCash.getEntityInfo(TestEntity.class)).thenReturn(EntityInfoImpl.create(TestEntity.class));
            String result = generator.createTable(TestEntity.class);
            assertEquals(
                    "CREATE TABLE IF NOT EXISTS TestEntity (id BIGINT, date DATE);",
                    result
            );
        }
    }
}
