package com.github.object.persistence.sql.impl;

import com.github.object.persistence.common.EntityCash;
import com.github.object.persistence.common.EntityInfo;
import com.github.object.persistence.common.ReflectionUtils;
import com.github.object.persistence.sql.types.TypeMapper;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SqlGenerator {
    private static String INSERT_INTO = "INSERT INTO %s (";
    private static String VALUES = "VALUES ('";
    private static String VALUES_SEPARATOR = "', '";
    private static String VALUES_SUFFIX = "');";
    private static String SUFFIX = ")";
    private static String SEPARATOR = ", ";

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
        String tableStartScript = String.format("CREATE TABLE IF NOT EXISTS %s (", entityClass.getSimpleName());
        return Arrays.stream(entityClass.getDeclaredFields())
                .map(this::identifySqlType)
                .collect(Collectors.joining(SEPARATOR, tableStartScript, SUFFIX + ";"));
    }

    private String identifySqlType(Field field) {
        String sqlType = TypeMapper.INSTANCE.getJDBCType(field.getType());
        String fieldName = field.getType().getSimpleName();
        if (sqlType == null) {
            if (field.isAnnotationPresent(OneToOne.class)) {
                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                return oneToOne.mappedBy() == null ? getIdNameOfFieldClass(field) : "";
            } else if (field.isAnnotationPresent(ManyToOne.class)) {
                Field id = getIdOfFieldClass(field);

                String idSqlType = TypeMapper.INSTANCE.getJDBCType(id.getType());

                return getIdNameOfFieldClass(field) + " " + idSqlType;
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                return "";
            } else {
                String message = String.format("Unexpected type of entity %s field %s", field.getDeclaringClass().getSimpleName(), field.getName());
                throw new IllegalStateException(message);
            }
        }

        return fieldName + " " + sqlType;
    }

    private String getIdNameOfFieldClass(Field field) {
        return String.format("%s_id", field.getType().getSimpleName().toLowerCase());
    }

    private Field getIdOfFieldClass(Field field) {
        return ReflectionUtils.getId(field.getType());
    }


    /**
     * Вставляет запись в существующую таблицу
     *
     * @param entity запись, которую необходимо вставить
     * @return сгенерированный SQL-код
     */
    String insertRecord(Object entity) {
        EntityInfo<?> info = EntityCash.getEntityInfo(entity.getClass());
        //анной функцией можно еще и manyTo1 обрабатывать
        handleParentOneToOne(info.getOneToOneFields(true), entity);

//        String insertScript = String.format(INSERT_INTO, info.getEntityName());
//        String columns = info.getFields().stream().map(field -> handleColumns(entity, field))
//                .collect(Collectors.joining(SEPARATOR, insertScript, SUFFIX));
//        String values = info.getFields().stream()
//                .map(field -> handleValues(entity, field))
//                .collect(Collectors.joining(VALUES_SEPARATOR, VALUES, VALUES_SUFFIX));
//        return columns + values;
        return null;
    }

    /**
     * Возвращает карту имя поля : значение поля где поле это не энтити-сущность
     *
     * @param fields
     * @param entity
     * @return
     */
    private Map<String, String> handleColumns(Set<Field> fields, Object entity) {
        return null;
    }

    /**
     * Создает отдельную карту со значениями для сущностей в коллекции (элемент fields это коллекция)
     *
     * @param fields
     * @param entity
     * @return
     */
    private Map<String, String> handleOneToMany(Set<Field> fields, Object entity) {
        return null;
    }

    /**
     * Возвращает карту имя поля : значение поля где поле это id связанной сущности, причем
     * переданная entity хранит значение ключа
     *
     * @param fields поля, аннотированные OneToOne
     * @param entity сущность содержащая связи
     * @return карта, где ключ -- имя поля в таблице, а значение его величина в виде строки
     */
    private Map<String, String> handleParentOneToOne(Set<Field> fields, Object entity) {
        return fields.stream().map(field -> {
                    Field id = getIdOfFieldClass(field);
                    Object fieldValue = ReflectionUtils.getValueFromField(entity, field);
                    Object idValue = ReflectionUtils.getValueFromField(fieldValue, id);
                    return Map.entry(getIdNameOfFieldClass(field), idValue.toString());
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
}
