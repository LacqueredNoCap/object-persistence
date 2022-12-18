package com.github.object.persistence.sql.types;

import java.sql.JDBCType;

public class ShortType implements TypeWrapper<Short> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.SMALLINT;
    }

    @Override
    public Class<Short> getJavaType() {
        return Short.class;
    }
}
