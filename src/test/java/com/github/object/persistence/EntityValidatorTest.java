package com.github.object.persistence;

import com.github.object.persistence.common.EntityValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EntityValidatorTest {
    private final EntityValidator parser = new EntityValidator();

    @Test
    void parse_TestEntityClass_createdValidSqlScript(){

        assertDoesNotThrow(() -> parser.validateEntity(TestEntity.class));
    }
}