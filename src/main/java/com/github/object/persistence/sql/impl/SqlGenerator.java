package com.github.object.persistence.sql.impl;

import com.github.object.persistence.common.ReflectionUtils;
import com.github.object.persistence.sql.types.TypeMapper;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SqlGenerator {

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
                .collect(Collectors.joining(", ", tableStartScript, ");"));
    }

    private String identifySqlType(Field field) {
        String sqlType = TypeMapper.INSTANCE.getJDBCType(field.getType());
        String fieldName = field.getType().getSimpleName();
        if (sqlType == null) {
            if (field.isAnnotationPresent(OneToOne.class)) {
                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                return oneToOne.mappedBy() == null ? getIdName(field) : "";
            } else if (field.isAnnotationPresent(ManyToOne.class)) {
                return getIdName(field);
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                return "";
            } else {
                String message = String.format("Unexpected type of entity %s field %s", field.getDeclaringClass().getSimpleName(), field.getName());
                throw new IllegalStateException(message);
            }
        }

        return fieldName + " " + sqlType;
    }

    private String getIdName(Field field) {
        Field id = ReflectionUtils.INSTANCE.getId(field.getType());

        String sqlType = TypeMapper.INSTANCE.getJDBCType(id.getType());

        return String.format("%s_id %s", field.getType().getSimpleName().toLowerCase(), sqlType);
    }

    /**
     * Вставляет запись в существующую таблицу
     *
     * @param entity запись, которую необходимо вставить
     * @return сгенерированный SQL-код
     */
    <T> String insertRecord(T entity) {
        return null;
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
