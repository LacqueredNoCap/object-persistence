package com.github.object.persistence.sql.types;

import java.sql.JDBCType;
import java.time.Instant;

public class TimestampType implements TypeWrapper<Instant> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.TIMESTAMP;
    }

    @Override
    public Class<Instant> getJavaType() {
        return Instant.class;
    }
}
