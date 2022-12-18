package com.github.object.persistence.common;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionUtils {
    public static final ReflectionUtils INSTANCE = new ReflectionUtils();
    private ReflectionUtils(){
    }

    public List<Field> getIds(Class<?> entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .collect(Collectors.toList());
    }

    public Field getId(Class<?> entity) {
        return getIds(entity)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Id on entity %s not found", entity.getSimpleName())
                ));
    }

    public Class<?> getGenericType(Field field) {
        ParameterizedType fieldListType = (ParameterizedType) field.getGenericType();
        return (Class<?>) fieldListType.getActualTypeArguments()[0];
    }
}
