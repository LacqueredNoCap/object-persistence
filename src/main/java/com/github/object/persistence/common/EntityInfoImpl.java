package com.github.object.persistence.common;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityInfoImpl<T> implements EntityInfo<T> {
    private final Class<T> type;

    private EntityInfoImpl(Class<T> type) {
        this.type = type;
    }

    public static EntityInfo<?> create(Class<?> type) {
        return new EntityInfoImpl<>(type);
    }

    @Override
    public T getProxy(MethodInterceptor whatToProxy) {
        return type.cast(Enhancer.create(type, whatToProxy));
    }

    @Override
    public Class<?> getFieldClassTypeByName(String fieldName) {
        return getFieldByName(fieldName)
                .getType();
    }

    @Override
    public Set<Class<?>> getAnnotations(String fieldName) {
        return Arrays.stream(getFieldByName(fieldName).getAnnotations())
                .map(Annotation::annotationType)
                .collect(Collectors.toSet());
    }

    @Override
    public String getEntityName() {
        return type.getSimpleName();
    }

    @Override
    public Map<String, Class<?>> getFieldNames() {
        return Arrays.stream(type.getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, Field::getType));
    }

    private Field getFieldByName(String fieldName) {
        String message = String.format("Field %s in entity %s not found", fieldName, getEntityName());
        return Arrays.stream(type.getFields())
                .findFirst()
                .orElseThrow(() -> new FieldNotFoundInEntityException(message));
    }
}
