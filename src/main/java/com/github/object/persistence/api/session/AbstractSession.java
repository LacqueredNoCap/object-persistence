package com.github.object.persistence.api.session;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractSession implements Session {

    public abstract <T> List<T> getRecords(Class<T> entityClass, Optional<String> predicate);

    public abstract <T> long updateRecord(Class<T> entityClass, Map<String, Object> fieldValueMap, Optional<String> predicate);

    public abstract <T> void deleteRecord(Class<T> entityClass, Optional<String> predicate);

}
