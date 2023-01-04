package com.github.object.persistence.sql.impl;

import com.github.object.persistence.common.DataSourceWrapper;
import com.github.object.persistence.common.EntityCash;
import com.github.object.persistence.common.EntityInfo;
import com.github.object.persistence.common.NullWrapper;
import com.github.object.persistence.common.utils.CollectionUtils;
import com.github.object.persistence.common.utils.FieldUtils;
import com.github.object.persistence.common.utils.ReflectionUtils;
import com.github.object.persistence.exception.ExecuteException;
import com.github.object.persistence.sql.types.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FromSqlToObjectMapper<R extends Connection> {
    private final SqlGenerator generator;
    private final Logger logger = LoggerFactory.getLogger(FromSqlToObjectMapper.class);
    private static final String PREDICATE = "%s = %s";


    public FromSqlToObjectMapper(SqlGenerator generator) {
        this.generator = generator;
    }

    <T> boolean createTable(DataSourceWrapper<R> wrapper, Class<T> entityClass) {
        String script = generator.createTable(entityClass);
        try (PreparedStatement statement = wrapper.getSource().prepareStatement(script)) {
            statement.executeUpdate();
            return true;
        } catch (Exception exception) {
            logger.error("Exception during create", exception);
            return false;
        }
    }

    <T> boolean insert(DataSourceWrapper<R> wrapper, T entity) {
        handleOneToOneForInsert(entity, wrapper);
        handleOneToManyForInsert(entity, wrapper);
        Map<String, Object> fieldNameValueMap = prepareEntityFieldValues(entity);
        String script = generator.insertRecord(entity.getClass(), fieldNameValueMap.keySet());
        IntStream fieldValueRange = IntStream.rangeClosed(1, fieldNameValueMap.size());
        try (PreparedStatement statement = wrapper.getSource().prepareStatement(script)) {
            Iterator<Object> valueIterator = fieldNameValueMap.values().iterator();
            fieldValueRange.forEach(currentIndex -> setObjectToStatement(valueIterator.next(), currentIndex, statement));
            return statement.executeUpdate() == 1;
        } catch (Exception exception) {
            logger.error("Exception during insert", exception);
            return false;
        }
    }

    <T> boolean insert(DataSourceWrapper<R> wrapper, Collection<T> records) {
        if (records.isEmpty()) {
            return true;
        } else {
            Class<?> collectionClass = ReflectionUtils.getClassOfCollection(records);
            Map<String, Deque<Object>> valuesToInsert = new HashMap<>();

            for (T entity : records) {
                handleOneToOneForInsert(entity, wrapper);
                handleOneToManyForInsert(entity, wrapper);
                prepareEntityFieldValues(entity).forEach((key, value) ->
                        CollectionUtils.putIfAbsent(key, value, valuesToInsert));
            }
            int sizeOfRecords = records.size();
            IntStream fieldValueRange = IntStream.rangeClosed(
                    1,
                    sizeOfRecords * valuesToInsert.size()
            );
            String script = generator.insertRecords(collectionClass, records.size(), valuesToInsert.keySet());
            try (PreparedStatement statement = wrapper.getSource().prepareStatement(script)) {
                AtomicReference<Iterator<Deque<Object>>> valueIterator = new AtomicReference<>(valuesToInsert.values().iterator());
                fieldValueRange.forEach(currentIndex -> {
                    if (valueIterator.get().hasNext()) {
                        setObjectToStatement(valueIterator.get().next().pollFirst(), currentIndex, statement);
                    } else {
                        valueIterator.set(valuesToInsert.values().iterator());
                        setObjectToStatement(valueIterator.get().next().pollFirst(), currentIndex, statement);
                    }
                });
                return statement.executeUpdate() == sizeOfRecords;
            } catch (Exception exception) {
                logger.error("Exception during insert", exception);
                return false;
            }
        }
    }

    <T> List<T> get(DataSourceWrapper<R> wrapper, Class<T> entityClass, String predicate) {
        String script = generator.getFromTableWithPredicate(entityClass, predicate);
        try (PreparedStatement statement = wrapper.getSource()
                .prepareStatement(script, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery();
            List<T> resultList = new ArrayList<>();
            while (resultSet.next()) {
                T entity = getWithStatement(wrapper, ReflectionUtils.createEmptyInstance(entityClass), resultSet);
                resultList.add(entity);
            }
            return resultList;
        } catch (Exception e) {
            logger.error("Exception during get", e);
            throw new ExecuteException(e);
        }
    }

    <T> void delete(DataSourceWrapper<R> wrapper, Class<T> entityClass, String predicate) {
        String script = generator.getFromTableWithPredicate(entityClass, predicate);
        try (PreparedStatement statement = wrapper.getSource()
                .prepareStatement(script, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet resultSet = statement.executeQuery();
            EntityInfo<T> info = EntityCash.getEntityInfo(entityClass);
            while (resultSet.next()) {
                Object idValue = resultSet.getObject(info.getIdField().getName());

            }
        } catch (Exception e) {
            logger.error("Exception during delete", e);
            throw new ExecuteException(e);
        }
    }

    <T> void delete(DataSourceWrapper<R> wrapper, T entity) {
        Map<Class<?>, Deque<String>> relatedEntities = handleCascadeDelete(entity);
        for (Map.Entry<Class<?>, Deque<String>> entry : relatedEntities.entrySet()) {
            entry.getValue().forEach(where -> delete(wrapper, entry.getKey(), where));
        }
    }

    private <T> T getWithStatement(DataSourceWrapper<R> wrapper, T entity, ResultSet resultSet) throws SQLException {
        EntityInfo<T> info = EntityCash.getEntityInfo(entity);

        for (Field field : info.getNoRelationFields()) {
            Object tableValue = resultSet.getObject(field.getName());
            ReflectionUtils.setValueToField(entity, field, tableValue);
        }

        for (Field field : info.getOneToOneFields(true)) {
            if (ReflectionUtils.getValueFromField(entity, field) == null) {
                Object foreignKey = resultSet.getObject(FieldUtils.getForeignKeyName(field));
                Object processesRelation = null;
                if (foreignKey != null) {
                    processesRelation = getOneToOneParent(
                            wrapper,
                            field.getType(),
                            field.getName(),
                            entity,
                            String.format(PREDICATE, FieldUtils.getIdNameOfFieldClass(field.getType()), foreignKey));
                }
                ReflectionUtils.setValueToField(entity, field, processesRelation);
            }
        }

        for (Field field : info.getOneToOneFields(false)) {
            if (ReflectionUtils.getValueFromField(entity, field) == null) {
                Object idValue = ReflectionUtils.getValueFromField(entity, info.getIdField());
                OneToOne annotation = field.getAnnotation(OneToOne.class);
                Object processesRelation = getOneToOneChild(
                        wrapper,
                        field.getType(),
                        annotation.mappedBy(),
                        entity,
                        String.format(PREDICATE, FieldUtils.getForeignKeyName(
                                ReflectionUtils.getFieldByName(field.getType(), annotation.mappedBy())
                        ), idValue));
                ReflectionUtils.setValueToField(entity, field, processesRelation);
            }
        }

        for (Field field : info.getManyToOneFields()) {
            if (ReflectionUtils.getValueFromField(entity, field) == null) {
                Object foreignKey = resultSet.getObject(FieldUtils.getForeignKeyName(field));
                Object processesRelation = getManyToOne(wrapper, field.getType(), field.getName(), entity,
                        String.format(PREDICATE, FieldUtils.getIdNameOfFieldClass(field.getType()), foreignKey));
                ReflectionUtils.setValueToField(
                        entity,
                        field,
                        processesRelation
                );
            }
        }

        for (Field field : info.getOneToManyFields()) {
            Object idValue = ReflectionUtils.getValueFromField(entity, info.getIdField());
            OneToMany annotation = field.getAnnotation(OneToMany.class);
            Class<?> parentClass = ReflectionUtils.getGenericType(field);
            EntityInfo<?> parentInfo = EntityCash.getEntityInfo(parentClass);
            Field targetField = getParentMappedByField(parentInfo.getManyToOneFields(), entity.getClass(), annotation.mappedBy());

            Collection<?> processesRelation = getOneToMany(
                    wrapper,
                    parentClass,
                    annotation.mappedBy(),
                    entity,
                    field,
                    String.format(PREDICATE, FieldUtils.getForeignKeyName(targetField), idValue));
            ReflectionUtils.setValueToField(entity, field, processesRelation);
        }

        return entity;
    }

    private <T, P> T getManyToOne(
            DataSourceWrapper<R> wrapper,
            Class<T> childClass,
            String fieldName,
            P parentValue,
            String predicate
    ) throws SQLException {
        String script = generator.getFromTableWithPredicate(childClass, predicate);
        try (PreparedStatement statement = wrapper.getSource()
                .prepareStatement(script, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            EntityInfo<T> info = EntityCash.getEntityInfo(childClass);
            ResultSet resultSet = statement.executeQuery();
            T childEntity = ReflectionUtils.createEmptyInstance(childClass);
            Field oneToMany = getChildMappedByField(info.getOneToManyFields(), parentValue.getClass(),
                    field -> field.getAnnotation(OneToMany.class).mappedBy().equals(fieldName));
            resultSet.next();
            if (oneToMany == null) {
                return getWithStatement(wrapper, childEntity, resultSet);
            }

            Collection<P> collection = decideTypeOfCollection(oneToMany);
            collection.add(parentValue);
            ReflectionUtils.setValueToField(childEntity, oneToMany, collection);
            return getWithStatement(wrapper, childEntity, resultSet);
        }
    }

    private <P> Collection<P> decideTypeOfCollection(Field parent) {
        Collection<P> oneToMany;
        if (List.class.isAssignableFrom(parent.getType())) {
            oneToMany = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(parent.getType())) {
            oneToMany = new HashSet<>();
        } else if (Queue.class.isAssignableFrom(parent.getType())) {
            oneToMany = new LinkedList<>();
        } else {
            throw new IllegalStateException(String.format("Unexpected type of Collection: %s", parent.getType()));
        }
        return oneToMany;
    }

    private <T> Collection<T> getOneToMany(
            DataSourceWrapper<R> wrapper,
            Class<T> parentClass,
            String mappedBy,
            Object childValue,
            Field collectionField,
            String predicate
    ) throws SQLException {
        String script = generator.getFromTableWithPredicate(parentClass, predicate);
        EntityInfo<T> parentInfo = EntityCash.getEntityInfo(parentClass);
        try (PreparedStatement statement = wrapper.getSource()
                .prepareStatement(script, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery();
            Collection<T> collection = (Collection<T>) ReflectionUtils.getValueFromField(childValue, collectionField);
            if (collection == null) {
                collection = decideTypeOfCollection(collectionField);
            }
            Field parentId = parentInfo.getIdField();
            while (resultSet.next()) {
                Object idValue = resultSet.getObject(parentId.getName());
                if (collection.stream()
                        .filter(parentItem -> ReflectionUtils.getValueFromField(parentItem, parentId).equals(idValue))
                        .count() == 0) {
                    T parentEntity = ReflectionUtils.createEmptyInstance(parentClass);
                    ReflectionUtils.setValueToField(parentEntity, ReflectionUtils.getFieldByName(parentClass, mappedBy), childValue);
                    getWithStatement(wrapper, parentEntity, resultSet);
                    collection.add(parentEntity);
                }
            }
            return collection;
        }
    }


    private <T> T getOneToOneChild(
            DataSourceWrapper<R> wrapper,
            Class<T> parentClass,
            String mappedBy,
            Object childValue,
            String predicate
    ) throws SQLException {
        String script = generator.getFromTableWithPredicate(parentClass, predicate);
        try (PreparedStatement statement = wrapper.getSource()
                .prepareStatement(script, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }
            T parentEntity = ReflectionUtils.createEmptyInstance(parentClass);
            ReflectionUtils.setValueToField(parentEntity, ReflectionUtils.getFieldByName(parentClass, mappedBy), childValue);
            return getWithStatement(wrapper, parentEntity, resultSet);
        }
    }

    private <T> T getOneToOneParent(
            DataSourceWrapper<R> wrapper,
            Class<T> childClass,
            String fieldName,
            Object parentValue,
            String predicate
    ) throws SQLException {
        String script = generator.getFromTableWithPredicate(childClass, predicate);
        try (PreparedStatement statement = wrapper.getSource()
                .prepareStatement(script, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            EntityInfo<T> info = EntityCash.getEntityInfo(childClass);
            ResultSet resultSet = statement.executeQuery();
            T childEntity = ReflectionUtils.createEmptyInstance(childClass);
            Field oneToOneChild = getChildMappedByField(info.getOneToOneFields(false), parentValue.getClass(),
                    field -> field.getAnnotation(OneToOne.class).mappedBy().equals(fieldName));
            resultSet.next();
            if (oneToOneChild == null) {
                return getWithStatement(wrapper, childEntity, resultSet);
            }

            ReflectionUtils.setValueToField(childEntity, oneToOneChild, parentValue);
            return getWithStatement(wrapper, childEntity, resultSet);
        }
    }

    private Field getChildMappedByField(Collection<Field> fields,
                                        Class<?> parentClass,
                                        Predicate<Field> annotationCondition) {
        long count = fields.stream().filter(field -> ReflectionUtils.getGenericType(field).equals(parentClass)).count();
        if (count > 1) {
            return fields.stream()
                    .filter(field -> ReflectionUtils.getGenericType(field).equals(parentClass))
                    .filter(annotationCondition)
                    .findFirst().orElseThrow(IllegalStateException::new);
        } else if (count == 0) {
            return null;
        } else {
            return fields.stream()
                    .filter(field -> ReflectionUtils.getGenericType(field).equals(parentClass))
                    .findFirst().orElseThrow(IllegalStateException::new);
        }
    }

    private Field getParentMappedByField(Set<Field> fields, Class<?> childClass, String mappedBy) {
        long count = fields.stream().filter(field -> field.getType().equals(childClass)).count();
        if (count > 1) {
            return fields.stream().filter(field -> field.getType().equals(childClass))
                    .filter(field -> field.getName().equals(mappedBy))
                    .findFirst().orElseThrow(IllegalStateException::new);
        } else if (count == 0) {
            /* необходима поддержка односторонних child relation? Актуально только в случае OneToMany
            (когда в parent классе нету ни одной Many2-1 связи, в гибернейте создается отдельная таблица связи) */
            throw new UnsupportedOperationException();
        } else {
            return fields.stream().filter(field -> field.getType().equals(childClass))
                    .findFirst().orElseThrow(IllegalStateException::new);
        }
    }


    private void setObjectToStatement(Object object, Integer index, PreparedStatement statement) {
        try {
            Class<?> objectType = object.getClass();
            if (objectType.equals(NullWrapper.class)) {
                statement.setNull(index, TypeMapper.INSTANCE.getSQLType(((NullWrapper) object).getType()).getVendorTypeNumber());
            } else {
                statement.setObject(index, object, TypeMapper.INSTANCE.getSQLType(objectType).getVendorTypeNumber());
            }
        } catch (SQLException e) {
            logger.error("Exception during preparing statement", e);
            throw new ExecuteException(e);
        }
    }

    private Map<String, Object> handleColumns(Set<Field> fields, Object entity) {
        return fields.stream().map(field -> {
            String fieldName = field.getName();
            TypeMapper.INSTANCE.validateSupportedType(field.getType());
            Object value = ReflectionUtils.getValueFromField(entity, field);
            return Map.entry(fieldName, value);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Object> prepareEntityFieldValues(Object entity) {
        EntityInfo<?> info = EntityCash.getEntityInfo(entity.getClass());

        Map<String, Object> oneToOneValues = handleParentRelation(info.getOneToOneFields(true), entity);
        Map<String, Object> manyToOneValues = handleParentRelation(info.getManyToOneFields(), entity);
        Map<String, Object> columns = handleColumns(info.getNoRelationFields(), entity);
        return Stream.of(oneToOneValues, manyToOneValues, columns)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Object> handleParentRelation(Set<Field> fields, Object entity) {
        return fields.stream()
                .collect(Collectors.toMap(FieldUtils::getForeignKeyName, field -> {
                    Field id = FieldUtils.getIdFieldOfGivenFieldClass(field);
                    Object fieldValue = ReflectionUtils.getValueFromField(entity, field);
                    if (fieldValue == null) {
                        return new NullWrapper(id.getType());
                    }
                    return ReflectionUtils.getValueFromField(fieldValue, id);
                }));
    }

    private void handleOneToManyForInsert(Object entity, DataSourceWrapper<R> wrapper) {
        Set<Field> oneToMany = EntityCash.getEntityInfo(entity).getOneToManyFields();

        if (!oneToMany.isEmpty()) {
            for (Field field : oneToMany) {
                Collection<?> collection = (Collection<?>) ReflectionUtils.getValueFromField(entity, field);
                if (collection != null && !insert(wrapper, collection)) {
                    throw new ExecuteException("Argument mismatch during execution of insert script");
                }
            }
        }
    }

    private void handleOneToOneForInsert(Object entity, DataSourceWrapper<R> wrapper) {
        Set<Field> oneToOne = EntityCash.getEntityInfo(entity).getOneToOneFields(true);

        if (!oneToOne.isEmpty()) {
            for (Field field : oneToOne) {
                Object relatedEntity = ReflectionUtils.getValueFromField(entity, field);
                if (relatedEntity != null && !insert(wrapper, relatedEntity)) {
                    throw new ExecuteException("Argument mismatch during execution of insert script");
                }
            }
        }
    }

    private Map<Class<?>, Deque<String>> handleCascadeDelete(Object entity) {
        EntityInfo<?> info = EntityCash.getEntityInfo(entity);
        Map<Class<?>, Deque<String>> result = new HashMap<>();

        for (Field field : info.getOneToOneFields(false)) {
            Object targetValue = ReflectionUtils.getValueFromField(entity, field);
            if (targetValue != null) {
                Object id = FieldUtils.getIdValue(entity);
                OneToOne annotation = field.getAnnotation(OneToOne.class);
                Field targetField = ReflectionUtils.getFieldByName(field.getType(), annotation.mappedBy());
                CollectionUtils.putIfAbsent(
                        field.getType(),
                        String.format(PREDICATE, FieldUtils.getForeignKeyName(targetField), id),
                        result
                );
            }
        }

        for (Field field : info.getOneToManyFields()) {
            Collection<?> targetValue = (Collection<?>) ReflectionUtils.getValueFromField(entity, field);
            if (targetValue != null && !targetValue.isEmpty()) {
                Object id = FieldUtils.getIdValue(entity);
                Class<?> collectionClass = ReflectionUtils.getGenericType(field);
                OneToMany annotation = field.getAnnotation(OneToMany.class);
                Field targetField = ReflectionUtils.getFieldByName(collectionClass, annotation.mappedBy());
                CollectionUtils.putIfAbsent(
                        collectionClass,
                        String.format(PREDICATE, FieldUtils.getForeignKeyName(targetField), id),
                        result
                );
            }
        }
        return result;
    }

    private Map<Class<?>, Deque<String>> handleCascadeDelete(ResultSet resultSet, Class<?> entityClass) {
        return null;
    }
}
