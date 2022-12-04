package com.github.object.persistence.implementation.sql;

public interface SQLGenerator {

    /**
     * Создает таблицу на основе переданного записи
     *
     * @param entityClass класс с описанием таблицы
     *
     * @return сгенерированный SQL-код
     */
    <T> String createTable(Class<T> entityClass);

    /**
     * Вставляет запись в существующую таблицу
     *
     * @param entity запись, которую необходимо вставить
     *
     * @return сгенерированный SQL-код
     */
    <T> String insertRecord(T entity);

    /**
     * Достает запись из таблицы по идентификатору
     *
     * @param entityClass класс с описанием таблицы
     * @param id         идентификатор
     *
     * @return сгенерированный SQL-код
     */
    <T, R> String getFromTable(Class<T> entityClass, R id);

    /**
     * Удаление записи из таблицы по идентификатору
     *
     * @param entityClass класс с описанием таблицы
     * @param id         идентификатор
     *
     * @return сгенерированный SQL-код
     */
    <T, R> String deleteById(Class<T> entityClass, R id);

    /**
     * Обновление записи в таблице.
     *
     * @param entity запись, которая будет обновлена в таблице
     *
     * @return сгенерированный SQL-код
     */
    <T> String updateRecord(T entity);

}
