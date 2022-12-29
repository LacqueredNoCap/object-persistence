package com.github.object.persistence.common;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityInfoImpl<T> implements EntityInfo<T> {
    private final Class<T> type;
    private final Set<Field> fields = initFields();
    private final Map<String, Field> fieldsMap = initFieldsMap();
    private final Set<Field> manyToOneFields = getFieldsWithAnnotation(ManyToOne.class);
    private final Set<Field> oneToOneFields = getFieldsWithAnnotation(OneToOne.class);
    private final Set<Field> oneToManyFields = getFieldsWithAnnotation(OneToMany.class);

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
        return getFieldByName(fieldName).getType();
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
    public Map<String, Field> getFieldNames() {
        return fieldsMap;
    }

    @Override
    public Set<Field> getFields() {
        return fields;
    }

    public Set<Field> getManyToOneFields() {
        return manyToOneFields;
    }

    public Set<Field> getOneToOneFields(boolean parent) {
        return oneToOneFields.stream()
                .filter(field -> filterOnParent(parent, field))
                .collect(Collectors.toSet());
    }

    private boolean filterOnParent(boolean parent, Field field) {
        if (parent) {
            return !StringUtils.isBlank(field.getAnnotation(OneToOne.class).mappedBy());
        } else {
            return StringUtils.isBlank(field.getAnnotation(OneToOne.class).mappedBy());
        }
    }

    public Set<Field> getOneToManyFields() {
        return oneToManyFields;
    }

    private Set<Field> initFields() {
        return Arrays.stream(type.getDeclaredFields())
                .collect(Collectors.toSet());
    }

    private Map<String, Field> initFieldsMap() {
        return fields.stream().collect(Collectors.toMap(Field::getName, field -> field));
    }

    public Set<Field> getFieldsWithAnnotation(Class<? extends Annotation> annotationClass) {
        return fields.stream().filter(field -> field.isAnnotationPresent(annotationClass)).collect(Collectors.toSet());
    }

    private Field getFieldByName(String fieldName) {
        String message = String.format("Field %s in entity %s not found", fieldName, getEntityName());
        Field value = fieldsMap.get(fieldName);
        if (value == null) throw new FieldNotFoundInEntityException(message);
        return value;
    }
}
