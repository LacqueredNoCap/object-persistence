package com.github.object.persistence.sql.types;

import java.sql.JDBCType;

public class StringType implements TypeWrapper<String> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.VARCHAR;
    }

    public Class<String> getJavaType() {
        return String.class;
    }
}
