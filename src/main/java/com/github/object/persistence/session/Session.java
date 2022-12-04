package com.github.object.persistence.session;

public interface Session {
    /**
     * Достать сущность из бд по id
     *
     * @param entityClass класс сущности
     * @param id          идентификатор
     * @param <T>         тип объекта
     * @param <R>         тип идентификатора
     * @return сущность, находящаяся в бд с данным идентификатором
     */
    <T, R> T getRecord(Class<T> entityClass, R id);

    /**
     * Сохранить или обновить сущность в бд
     *
     * @param entity сущность, которую необходимо сохранить
     * @param <T>    тип сущности
     * @return идентификатор, с котором была сохранена сущность
     */
    <T> Object saveOrUpdate(T entity);

    /**
     * Удалить сущность из бд
     *
     * @param entity сущность, которую необходимо сохранить
     * @param <T>    тип сущности
     */
    <T> void deleteRecord(T entity);
}
