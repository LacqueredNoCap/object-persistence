package com.github.object.persistence.sql.types;

import java.sql.JDBCType;

public class BinaryType implements TypeWrapper<byte[]> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.BINARY;
    }

    @Override
    public Class<byte[]> getJavaType() {
        return byte[].class;
    }
}
