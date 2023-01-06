package com.github.object.persistence.api.session;

import com.github.object.persistence.api.criteria.Query;

import java.util.Collection;
import java.util.List;

// TODO: Будет работать с DataSourceWrapper.
public interface Session extends AutoCloseable {

    <T> boolean createTable(Class<T> entityClass);

    /**
     * Достать сущность из хранилища по id.
     *
     * @param entityClass класс сущности,
     * @param id идентификатор.
     *
     * @return сущность, находящаяся в хранилище с данным идентификатором.
     */
    <T, R> T getRecord(Class<T> entityClass, R id);

    /**
     * Сохранить или обновить сущность в хранилище.
     *
     * @param entity сущность, которую необходимо сохранить.
     *
     * @return идентификатор, с котором была сохранена сущность.
     */
    <T> boolean saveOrUpdate(T entity);

    <T> boolean saveOrUpdate(Collection<T> entities);

    /**
     * Удалить сущность из хранилища.
     *
     * @param entity сущность, которую необходимо сохранить.
     */
    <T> void deleteRecord(T entity);

    /**
     * Предоставляет конфигуратор запросов по типу сущности.
     *
     * @param clazz тип сущности.
     *
     * @return конфигуратор запросов.
     */
    <T> Query<T> buildQuery(Class<T> clazz);

}
