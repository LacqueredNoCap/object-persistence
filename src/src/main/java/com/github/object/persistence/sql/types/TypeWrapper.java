package com.github.object.persistence.sql.types;

import java.sql.JDBCType;

public interface TypeWrapper<T> {
    JDBCType getSqlType();

    Class<T> getJavaType();
}
