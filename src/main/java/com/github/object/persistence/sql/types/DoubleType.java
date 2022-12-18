package com.github.object.persistence.sql.types;

import java.sql.JDBCType;

public class DoubleType implements TypeWrapper<Double> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.DOUBLE;
    }

    @Override
    public Class<Double> getJavaType() {
        return Double.class;
    }
}
