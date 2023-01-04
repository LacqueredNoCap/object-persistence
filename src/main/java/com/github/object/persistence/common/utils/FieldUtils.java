package com.github.object.persistence.common.utils;

import com.github.object.persistence.common.EntityCash;

import java.lang.reflect.Field;

public class FieldUtils {
    private FieldUtils() {
    }

    public static String getForeignKeyName(Field field) {
        return String.format("%s_id", field.getName());
    }

    public static Field getIdFieldOfGivenFieldClass(Field field) {
        return getIdFieldOfGivenFieldClass(field.getType());
    }

    public static String getIdNameOfFieldClass(Class<?> kClass) {
        return getIdFieldOfGivenFieldClass(kClass).getName();
    }

    public static Object getIdValue(Object owner) {
        Field id = getIdFieldOfGivenFieldClass(owner.getClass());
        return ReflectionUtils.getValueFromField(owner, id);
    }

    public static Object getIdValueOfParentRelation(Object parent, Field relatedField) {
        Field id = getIdFieldOfGivenFieldClass(relatedField);
        Object relatedValue = ReflectionUtils.getValueFromField(parent, relatedField);
        if (relatedValue != null) {
            return ReflectionUtils.getValueFromField(relatedValue, id);
        } else {
            return null;
        }
    }

    public static String getTableNameOfFieldClass(Field field) {
        return EntityCash.getEntityInfo(field.getType()).getEntityName();
    }

    private static Field getIdFieldOfGivenFieldClass(Class<?> kClass) {
        return EntityCash.getEntityInfo(kClass).getIdField();
    }
}
