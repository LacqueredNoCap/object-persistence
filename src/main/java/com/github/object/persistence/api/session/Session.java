package com.github.object.persistence.api.session;

import com.github.object.persistence.api.criteria.CriteriaQuery;

/**
 * TODO: Будет работать с DataSourceWrapper.
 */
public interface Session extends AutoCloseable {

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
    <T> Object saveOrUpdate(T entity);

    /**
     * Удалить сущность из хранилища.
     *
     * @param entity сущность, которую необходимо сохранить.
     */
    <T> void deleteRecord(T entity);

    /**
     * Предоставляет CriteriaQuery по типу сущности.
     *
     * @param clazz тип сущности.
     *
     * @return конфигурацию запроса.
     */
    <T> CriteriaQuery<T> getCriteriaQuery(Class<T> clazz);

}
