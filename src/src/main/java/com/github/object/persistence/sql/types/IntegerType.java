package com.github.object.persistence.sql.types;

import java.sql.JDBCType;

public class IntegerType implements TypeWrapper<Integer> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.INTEGER;
    }

    @Override
    public Class<Integer> getJavaType() {
        return Integer.class;
    }
}
