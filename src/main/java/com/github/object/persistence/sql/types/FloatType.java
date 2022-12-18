package com.github.object.persistence.sql.types;

import java.sql.JDBCType;

public class FloatType implements TypeWrapper<Float> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.REAL;
    }

    @Override
    public Class<Float> getJavaType() {
        return Float.class;
    }
}
