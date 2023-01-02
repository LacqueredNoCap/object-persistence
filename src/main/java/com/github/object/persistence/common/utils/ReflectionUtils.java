package com.github.object.persistence.common.utils;

import com.github.object.persistence.exception.ReflectionOperationException;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ReflectionUtils {

    private ReflectionUtils() {}

    public static List<Field> getIds(Class<?> entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .collect(Collectors.toList());
    }

    public static Field getId(Class<?> entity) {
        return getIds(entity)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Id on entity %s not found", entity.getSimpleName())
                ));
    }

    public static Class<?> getGenericType(Field field) {
        ParameterizedType fieldListType = (ParameterizedType) field.getGenericType();
        return (Class<?>) fieldListType.getActualTypeArguments()[0];
    }

    public static Object getValueFromField(Object fieldOwner, Field field) {
        try {
            field.setAccessible(true);
            return field.get(fieldOwner);
        } catch (IllegalAccessException exception) {
            throw new ReflectionOperationException(exception);
        } finally {
            field.setAccessible(false);
        }
    }
}
