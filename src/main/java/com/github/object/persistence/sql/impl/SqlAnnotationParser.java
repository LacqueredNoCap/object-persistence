package com.github.object.persistence.sql.impl;

import com.github.object.persistence.common.AnnotationParser;
import com.github.object.persistence.common.ReflectionUtils;
import com.github.object.persistence.exception.ValidationException;
import com.github.object.persistence.sql.types.TypeMapper;
import com.google.common.base.CaseFormat;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class SqlAnnotationParser implements AnnotationParser {
    @Override
    public String prepareTable(Class<?> entity) {
        validateClass(entity);
        String tableName = convertToSnakeCase(entity.getSimpleName());
        String tableStartScript = String.format("CREATE TABLE IF NOT EXISTS %s (", tableName);
        return Arrays.stream(entity.getDeclaredFields())
                .filter(field -> !field.getName().contains("this"))
                .map(field -> {
                    validateField(field);
                    String fieldName = convertToSnakeCase(field.getName());
                    return fieldName + " " + identifySqlType(field);
                })
                .collect(Collectors.joining(", ", tableStartScript, ");"));
    }

    private void validateClass(Class<?> entity) {
        String message;
        if (!entity.isAnnotationPresent(Entity.class)) {
            message = String.format("Using %s class without Entity annotation", entity.getSimpleName());
            throw new ValidationException(message);
        }
        if (ReflectionUtils.INSTANCE.getIds(entity).size() != 1) {
            message = String.format("Using %s class with several ids", entity.getSimpleName());
            throw new ValidationException(message);
        }

    }

    private String convertToSnakeCase(String camelCase) {
        String preparedString = camelCase.substring(0, 1).toLowerCase() + camelCase.substring(1);
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, preparedString);
    }

    private String identifySqlType(Field field) {
        String sqlType = TypeMapper.INSTANCE.getJDBCType(field.getType());
        String result;
        if (field.isAnnotationPresent(OneToOne.class)) {
            result = String.format(
                    "%s UNIQUE FOREIGN KEY REFERENCES %s(%s)",
                    sqlType,
                    convertToSnakeCase(field.getType().getSimpleName()),
                    convertToSnakeCase(ReflectionUtils.INSTANCE.getId(field.getType()).getName())
            );
        } else if (field.isAnnotationPresent(Id.class)) {
            result = String.format("%s PRIMARY KEY", sqlType);
        } else if (field.isAnnotationPresent(ManyToOne.class)) {
            result = String.format(
                    "%s FOREIGN KEY REFERENCES %s(%s)",
                    sqlType,
                    convertToSnakeCase(field.getType().getSimpleName()),
                    convertToSnakeCase(ReflectionUtils.INSTANCE.getId(field.getType()).getName())
            );
        } else {
            result = sqlType;
        }
        return result;
    }

    private void validateField(Field fieldType) {
        Class<?> fieldClass = fieldType.getType();
        if (Collection.class.isAssignableFrom(fieldClass)) {
            Class<?> collectionClass = ReflectionUtils.INSTANCE.getGenericType(fieldType);
            if (!(fieldClass.isPrimitive() || TypeMapper.INSTANCE.getJDBCType(fieldClass) == null)) {
                validateClass(collectionClass);
            }
            if (!fieldType.isAnnotationPresent(OneToMany.class)) {
                throwWithValidationMessage(fieldType, "List of values without relation annotation");
            }

        }
        if (fieldClass.isAnnotationPresent(Entity.class)) {
            validateClass(fieldClass);
            if (!fieldType.isAnnotationPresent(OneToOne.class)) {
                throwWithValidationMessage(fieldType, "Entity without relation annotation");
            }
        }
    }

    private void throwWithValidationMessage(Field fieldType, String message) {
        String errorMessage = String.format(
                "Error while processing field %s in class %s: %s",
                fieldType.getName(),
                fieldType.getDeclaringClass().getSimpleName(),
                message
        );
        throw new ValidationException(errorMessage);
    }
}
