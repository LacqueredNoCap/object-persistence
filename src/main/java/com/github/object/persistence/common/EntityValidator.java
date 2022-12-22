package com.github.object.persistence.common;

import com.github.object.persistence.exception.ValidationException;
import com.github.object.persistence.sql.types.TypeMapper;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class EntityValidator {
    public void validateEntity(Class<?> entity) {
        validateClass(entity);
        validateIds(entity);
        for (Field field : entity.getDeclaredFields()) {
            validateField(field);
        }
    }

    private void validateClass(Class<?> entity) {
        String message;
        if (!entity.isAnnotationPresent(Entity.class)) {
            message = String.format("Using %s class without Entity annotation", entity.getSimpleName());
            throw new ValidationException(message);
        }
    }

    private void validateIds(Class<?> entity) {
        List<Field> ids = ReflectionUtils.INSTANCE.getIds(entity);
        String message;
        if (ids.isEmpty()) {
            message = String.format("Id in entity class %s is not present", entity.getSimpleName());
            throw new ValidationException(message);
        }
        if (ids.size() != 1) {
            message = String.format("Using %s class with several ids", entity.getSimpleName());
            throw new ValidationException(message);
        }
    }

    private void validateField(Field fieldType) {
        Class<?> fieldClass = fieldType.getType();
        if (Collection.class.isAssignableFrom(fieldClass)) {
            Class<?> itemClass = ReflectionUtils.INSTANCE.getGenericType(fieldType);
            boolean forNotPrimitiveInListCondition = !(itemClass.isPrimitive() || TypeMapper.INSTANCE.getJDBCType(itemClass) == null)
                    && itemClass.isAnnotationPresent(Entity.class);
            if (forNotPrimitiveInListCondition) {
                throwWithValidationMessage(fieldType, "Using non-entity class as Collection parameter");
            }
            if (!fieldType.isAnnotationPresent(OneToMany.class)) {
                throwWithValidationMessage(fieldType, "List of values without relation annotation");
            }

        }
        boolean forSingleEntityAnnotationCondition = fieldClass.isAnnotationPresent(Entity.class) &&
                (!fieldType.isAnnotationPresent(OneToOne.class) || !fieldType.isAnnotationPresent(ManyToOne.class));

        if (forSingleEntityAnnotationCondition) {
            throwWithValidationMessage(fieldType, "Entity without relation annotation");
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
