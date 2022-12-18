package com.github.object.persistence.sql.types;

import java.math.BigDecimal;
import java.sql.JDBCType;

public class BigDecimalType implements TypeWrapper<BigDecimal> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.NUMERIC;
    }

    @Override
    public Class<BigDecimal> getJavaType() {
        return BigDecimal.class;
    }
}
