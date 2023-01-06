package com.github.object.persistence;

import com.github.object.persistence.common.EntityValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EntityValidatorTest {
    private final EntityValidator parser = EntityValidator.getInstance();

    @Test
    void parse_TestEntityClass_createdValidSqlScript() {

        assertDoesNotThrow(() -> parser.validateEntity(TestEntity.class));
    }
}