package com.github.object.persistence.sql.types;

import java.sql.JDBCType;
import java.time.LocalTime;

public class TimeType implements TypeWrapper<LocalTime> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.TIME;
    }

    @Override
    public Class<LocalTime> getJavaType() {
        return LocalTime.class;
    }
}
