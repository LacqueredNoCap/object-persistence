package com.github.object.persistence.common;

import net.sf.cglib.proxy.MethodInterceptor;

import java.util.Map;
import java.util.Set;

/**
 * @param <T> тип сущности
 */
public interface EntityInfo<T> {

    /*
    MethodInterceptor это перехватчик методов, так как мы не должны завязываться на реализации, он
    может и будет перехватывать любые гет-сет методы, но логика будет отлчичаться в зависимости от реализации
     */
    T getProxy(MethodInterceptor whatToProxy);

    Class<?> getFieldClassTypeByName(String fieldName);

    Set<Class<?>> getAnnotations(String fieldName);

    String getEntityName();

    Map<String, Class<?>> getFieldNames();
}
