package com.github.object.persistence.sql.types;

import java.sql.JDBCType;
import java.time.LocalDate;

public class DateType implements TypeWrapper<LocalDate> {
    @Override
    public JDBCType getSqlType() {
        return JDBCType.DATE;
    }

    @Override
    public Class<LocalDate> getJavaType() {
        return LocalDate.class;
    }
}
