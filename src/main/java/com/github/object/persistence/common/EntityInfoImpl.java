package com.github.object.persistence.common;

import com.github.object.persistence.common.utils.StringUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityInfoImpl<T> implements EntityInfo<T> {
    private final Class<T> type;
    private final Set<Field> fields;
    private final Map<String, Field> fieldsMap;
    private final Set<Field> manyToOneFields;
    private final Set<Field> oneToOneFields;
    private final Set<Field> oneToManyFields;
    private final Set<Field> noRelationFields;

    private EntityInfoImpl(Class<T> type) {
        this.type = type;
        this.fields = initFields();
        fieldsMap = initFieldsMap();
        manyToOneFields = getFieldsWithAnnotation(ManyToOne.class);
        oneToOneFields = getFieldsWithAnnotation(OneToOne.class);
        oneToManyFields = getFieldsWithAnnotation(OneToMany.class);
        noRelationFields = initNoRelationFields();
    }

    public static <T> EntityInfo<T> create(Class<T> type) {
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
        return type.getSimpleName().toLowerCase();
    }

    @Override
    public Map<String, Field> getFieldNames() {
        return fieldsMap;
    }

    @Override
    public Set<Field> getFields() {
        return fields;
    }

    public Set<Field> getTableFields() {
        return Stream.of(manyToOneFields, getOneToOneFields(true), noRelationFields)
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public Set<Field> getManyToOneFields() {
        return manyToOneFields;
    }

    public Field getIdField() {
        return getFieldsWithAnnotation(Id.class).stream().findFirst().orElseThrow(
                () -> new IllegalStateException(
                        String.format("Id on entity %s not found", getEntityName())
                ));
    }

    public Set<Field> getOneToOneFields(boolean parent) {
        return oneToOneFields.stream()
                .filter(field -> filterOnParent(parent, field))
                .collect(Collectors.toSet());
    }

    public Set<Field> getNoRelationFields() {
        return noRelationFields;
    }

    public Set<Field> getOneToManyFields() {
        return oneToManyFields;
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

    private boolean filterOnParent(boolean parent, Field field) {
        if (parent) {
            return StringUtils.isBlank(field.getAnnotation(OneToOne.class).mappedBy());
        } else {
            return !StringUtils.isBlank(field.getAnnotation(OneToOne.class).mappedBy());
        }
    }

    private Set<Field> initFields() {
        return Arrays.stream(type.getDeclaredFields())
                .collect(Collectors.toSet());
    }

    private Set<Field> initNoRelationFields() {
        return fields.stream()
                .filter(field -> !field.isAnnotationPresent(OneToOne.class))
                .filter(field -> !field.isAnnotationPresent(ManyToOne.class))
                .filter(field -> !field.isAnnotationPresent(OneToMany.class))
                .collect(Collectors.toSet());
    }

    private Map<String, Field> initFieldsMap() {
        return fields.stream().collect(Collectors.toMap(Field::getName, field -> field));
    }
}
