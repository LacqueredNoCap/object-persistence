package com.github.object.persistence.sql.types;

import java.sql.JDBCType;

public class LongType implements TypeWrapper<Long> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.BIGINT;
    }

    @Override
    public Class<Long> getJavaType() {
        return Long.class;
    }
}
