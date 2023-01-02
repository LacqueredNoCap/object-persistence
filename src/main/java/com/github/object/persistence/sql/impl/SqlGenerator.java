package com.github.object.persistence.sql.impl;

import com.github.object.persistence.common.EntityCash;
import com.github.object.persistence.common.EntityInfo;
import com.github.object.persistence.common.utils.CollectionUtils;
import com.github.object.persistence.common.utils.ReflectionUtils;
import com.github.object.persistence.common.utils.StringUtils;
import com.github.object.persistence.sql.types.TypeMapper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlGenerator {
    private static final String INSERT_INTO = "INSERT INTO %s (";
    private static final String VALUES = "VALUES ";
    private static final String VALUES_SEPARATOR = "', '";
    private static final String VALUES_SUFFIX = "');";
    private static final String CLOSE_PARENTHESIS = ")";
    private static final String SEPARATOR = ", ";
    private static final String OPEN_PARENTHESIS = "(";
    private static final String CREATE_SCRIPT = "CREATE TABLE IF NOT EXISTS %s (";

    private SqlGenerator() {
    }

    private static final SqlGenerator INSTANCE = new SqlGenerator();

    static SqlGenerator getInstance() {
        return INSTANCE;
    }

    /**
     * Создает таблицу на основе переданного записи
     *
     * @param entityClass класс с описанием таблицы
     * @return сгенерированный SQL-код
     */
    <T> String createTable(Class<T> entityClass) {
        EntityInfo<?> info = EntityCash.getEntityInfo(entityClass);
        Stream<String> parentRelation = Stream.concat(
                        info.getOneToOneFields(true).stream(),
                        info.getManyToOneFields().stream()
                )
                .map(this::getTypeAndFieldNameForParentRelation);
        Stream<String> noRelationStream = info.getNoRelationFields().stream()
                .map(this::getTypeAndNameForUnrelatedField);

        return prepareScriptWithColumns(
                Stream.concat(parentRelation, noRelationStream),
                info.getEntityName(),
                CREATE_SCRIPT,
                CLOSE_PARENTHESIS + ";"
        );
    }

    private String getTypeAndFieldNameForParentRelation(Field field) {
        Field id = getIdOfFieldClass(field);

        String idSqlType = TypeMapper.INSTANCE.getJDBCType(id.getType());

        return StringUtils.separateWithSpace(getIdNameOfFieldClass(field), idSqlType);
    }

    private String getTypeAndNameForUnrelatedField(Field field) {
        String sqlType = TypeMapper.INSTANCE.getJDBCType(field.getType());
        String fieldName = field.getName();
        if (sqlType == null) {
            String message = String.format("Unexpected type of entity %s field %s", field.getDeclaringClass().getSimpleName(), field.getName());
            throw new IllegalStateException(message);
        }
        return StringUtils.separateWithSpace(fieldName, sqlType);
    }


    /**
     * Вставляет запись в существующую таблицу
     *
     * @param entity запись, которую необходимо вставить
     * @return сгенерированный SQL-код
     */
    String insertRecord(Object entity) {
        EntityInfo<?> info = EntityCash.getEntityInfo(entity.getClass());
        Map<String, String> fieldInfo = prepareEntityFieldValues(entity);

        String firstPartOfScript = prepareScriptWithColumns(
                fieldInfo.keySet().stream(), info.getEntityName(), INSERT_INTO, CLOSE_PARENTHESIS
        );

        String values = fieldInfo.values().stream()
                .collect(Collectors.joining(
                        VALUES_SEPARATOR,
                        VALUES + OPEN_PARENTHESIS + "'",
                        VALUES_SUFFIX)
                );

        String insertScript = StringUtils.separateWithSpace(firstPartOfScript, values);

        Set<String> oneToMany = handleOneToMany(entity);
        if (!oneToMany.isEmpty()) {
            return StringUtils.separateWithSpace(insertScript, String.join(" ", oneToMany));
        }


        return insertScript;
    }

    String insertRecords(Collection<?> records, String entityName) {
        if (records.isEmpty()) {
            return "";
        } else {
            Map<String, Deque<String>> valuesToInsert = new HashMap<>();

            StringBuilder secondPartOfScriptBuilder = new StringBuilder();
            for (Object entity : records) {
                prepareEntityFieldValues(entity).forEach((key, value) ->
                        CollectionUtils.put(key, value, valuesToInsert));

                Set<String> oneToMany = handleOneToMany(entity);
                if (!oneToMany.isEmpty()) {
                    secondPartOfScriptBuilder.append(String.join(" ", oneToMany));
                }
            }

            String firstPartOfScript = prepareScriptWithColumns(
                    valuesToInsert.keySet().stream(), entityName, INSERT_INTO, CLOSE_PARENTHESIS
            );

            secondPartOfScriptBuilder.append(firstPartOfScript);
            secondPartOfScriptBuilder.append(" ");
            secondPartOfScriptBuilder.append(VALUES);
            Iterator<Deque<String>> valuesIterator;

            boolean stop = false;
            while (!stop) {
                secondPartOfScriptBuilder.append(OPEN_PARENTHESIS);
                valuesIterator = valuesToInsert.values().iterator();

                while (valuesIterator.hasNext()) {
                    Deque<String> currentQueue = valuesIterator.next();
                    String value = currentQueue.pollFirst();
                    if (valuesIterator.hasNext()) {
                        secondPartOfScriptBuilder.append(String.format("'%s', ", value));
                    } else {
                        secondPartOfScriptBuilder.append(String.format("'%s'", value));
                        if (currentQueue.isEmpty()) {
                            stop = true;
                            break;
                        }
                    }
                }
                if (stop) {
                    secondPartOfScriptBuilder.append(");");
                } else {
                    secondPartOfScriptBuilder.append("), ");
                }
            }

            return secondPartOfScriptBuilder.toString();
        }
    }

    /**
     * Достает запись из таблицы по идентификатору
     *
     * @param entityClass класс с описанием таблицы
     * @param id          идентификатор
     * @return сгенерированный SQL-код
     */
    <T, R> String getFromTable(Class<T> entityClass, R id) {
        return null;
    }

    /**
     * Удаление записи из таблицы по идентификатору
     *
     * @param entityClass класс с описанием таблицы
     * @param id          идентификатор
     * @return сгенерированный SQL-код
     */
    <T, R> String deleteById(Class<T> entityClass, R id) {
        return null;
    }

    /**
     * Обновление записи в таблице.
     *
     * @param entity запись, которая будет обновлена в таблице
     * @return сгенерированный SQL-код
     */
    <T> String updateRecord(T entity) {
        return null;
    }

    private String prepareScriptWithColumns(
            Stream<String> columnNames,
            String entityName,
            String script,
            String suffix
    ) {
        return columnNames.collect(Collectors.joining(
                SEPARATOR,
                String.format(script, entityName),
                suffix
        ));
    }

    private Map<String, String> handleColumns(Set<Field> fields, Object entity) {
        return fields.stream().map(field -> {
            String fieldName = field.getName();
            TypeMapper.INSTANCE.validateSupportedType(field.getType());
            Object value = ReflectionUtils.getValueFromField(entity, field);
            return Map.entry(fieldName, value.toString());
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Set<String> handleOneToMany(Object entity) {
        Set<Field> oneToMany = EntityCash.getEntityInfo(entity.getClass()).getOneToManyFields();

        if (!oneToMany.isEmpty()) {
            return oneToMany.stream()
                    .map(field -> {
                        Collection<?> collection = (Collection<?>) ReflectionUtils.getValueFromField(entity, field);
                        return insertRecords(collection, ReflectionUtils.getGenericType(field).getSimpleName());
                    }).collect(Collectors.toSet());
        } else {
            return Set.of();
        }
    }

    private Map<String, String> prepareEntityFieldValues(Object entity) {
        EntityInfo<?> info = EntityCash.getEntityInfo(entity.getClass());

        Map<String, String> oneToOneValues = handleParentRelation(info.getOneToOneFields(true), entity);
        Map<String, String> manyToOneValues = handleParentRelation(info.getManyToOneFields(), entity);
        Map<String, String> columns = handleColumns(info.getNoRelationFields(), entity);
        return Stream.of(oneToOneValues, manyToOneValues, columns)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, String> handleParentRelation(Set<Field> fields, Object entity) {
        return fields.stream().map(field -> {
                    Field id = getIdOfFieldClass(field);
                    Object fieldValue = ReflectionUtils.getValueFromField(entity, field);
                    Object idValue = ReflectionUtils.getValueFromField(fieldValue, id);
                    return Map.entry(getIdNameOfFieldClass(field), idValue.toString());
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String getIdNameOfFieldClass(Field field) {
        return String.format("%s_id", field.getType().getSimpleName().toLowerCase());
    }

    private Field getIdOfFieldClass(Field field) {
        return ReflectionUtils.getId(field.getType());
    }
}
