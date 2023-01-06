package com.github.object.persistence.sql.types;

import java.sql.JDBCType;

public class ByteType implements TypeWrapper<Byte> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.TINYINT;
    }

    @Override
    public Class<Byte> getJavaType() {
        return Byte.class;
    }
}
