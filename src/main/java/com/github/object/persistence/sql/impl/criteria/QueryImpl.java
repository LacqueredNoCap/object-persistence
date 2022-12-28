package com.github.object.persistence.sql.impl.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.Query;

public class QueryImpl<T> implements Query<T> {

    /**
     * TODO: EntityInfo будет предоставляться из кэша (Session).
     */
    private final EntityInfo<T> entityInfo;
    private QueryType queryType;
    private Optional<Predicate> predicate;

    private enum QueryType {
        SELECT("SELECT %s FROM %s"),
        UPDATE("UPDATE %s SET VALUES %s"),
        DELETE("DELETE FROM %s");

        QueryType(String pattern) {}
    }

    private QueryImpl(EntityInfo<T> entityInfo) {
        this.entityInfo = entityInfo;
        queryType = QueryType.SELECT;
        predicate = Optional.empty();
    }

    public static <T> Query<T> getQuery(EntityInfo<T> entityInfo) {
        return new QueryImpl<>(entityInfo);
    }

    @Override
    public List<T> selectWhere(Predicate predicate) {
        queryType = QueryType.SELECT;
        this.predicate = Optional.ofNullable(predicate);
        return new ArrayList<>();
    }

    @Override
    public long updateWhere(Predicate predicate) {
        queryType = QueryType.UPDATE;
        this.predicate = Optional.ofNullable(predicate);
        return 0;
    }

    @Override
    public long deleteWhere(Predicate predicate) {
        queryType = QueryType.DELETE;
        this.predicate = Optional.ofNullable(predicate);
        return 0;
    }

}
