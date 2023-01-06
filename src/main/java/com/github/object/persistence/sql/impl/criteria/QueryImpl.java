package com.github.object.persistence.sql.impl.criteria;

import com.github.object.persistence.api.criteria.Predicate;
import com.github.object.persistence.api.criteria.Query;
import com.github.object.persistence.common.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class QueryImpl<T> implements Query<T> {

    // TODO: EntityInfo будет предоставляться из кэша (Session)
    private final EntityInfo<T> entityInfo;
    private Optional<Predicate> predicate;

    private QueryImpl(EntityInfo<T> entityInfo) {
        this.entityInfo = entityInfo;
        predicate = Optional.empty();
    }

    public static <T> Query<T> getQuery(EntityInfo<T> entityInfo) {
        return new QueryImpl<>(entityInfo);
    }

    @Override
    public List<T> selectWhere(Predicate pred) {
        predicate = Optional.ofNullable(pred);
        //(List<T>) core.execute(Class<T> class, Optional<String> pred.asString())
        return new ArrayList<>();
    }

    @Override
    public long updateWhere(Map<String, Object> fieldValueMap, Predicate pred) {
        predicate = Optional.ofNullable(pred);
        return 0;
    }

    @Override
    public long deleteWhere(Predicate pred) {
        predicate = Optional.ofNullable(pred);
        return 0;
    }

}
