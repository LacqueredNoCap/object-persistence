package com.github.object.persistence.sql.types;

import java.sql.JDBCType;

public class BooleanType implements TypeWrapper<Boolean> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.BIT;
    }

    @Override
    public Class<Boolean> getJavaType() {
        return Boolean.class;
    }
}
