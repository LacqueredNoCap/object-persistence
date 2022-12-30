package com.github.object.persistence.sql.impl.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.Query;
import com.github.object.persistence.common.EntityInfo;

public class QueryImpl<T> implements Query<T> {

    // TODO: EntityInfo будет предоставляться из кэша (Session)
    private final EntityInfo<T> entityInfo;
    private QueryType queryType;
    private Optional<Predicate> predicate;

    private enum QueryType {
        SELECT("SELECT %s FROM %s"),
        UPDATE("UPDATE %s SET %s"),
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
    public List<T> selectWhere(Predicate pred) {
        queryType = QueryType.SELECT;
        predicate = Optional.ofNullable(pred);
        return new ArrayList<>();
    }

    @Override
    public long updateWhere(Predicate pred) {
        queryType = QueryType.UPDATE;
        predicate = Optional.ofNullable(pred);
        return 0;
    }

    @Override
    public long deleteWhere(Predicate pred) {
        queryType = QueryType.DELETE;
        predicate = Optional.ofNullable(pred);
        return 0;
    }

}
