package com.github.object.persistence.sql.types;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class TypeMapper {
    public static final TypeMapper INSTANCE = new TypeMapper();
    private final Map<Class<?>, JDBCType> types = initMap();

    private TypeMapper() {
    }

    //как минимум надо добавить timestamp_with_timezone
    private Map<Class<?>, JDBCType> initMap() {
        return Map.ofEntries(
                Map.entry(LocalTime.class, JDBCType.TIME),
                Map.entry(Float.class, JDBCType.REAL),
                Map.entry(Long.class, JDBCType.BIGINT),
                Map.entry(byte[].class, JDBCType.BINARY),
                Map.entry(Instant.class, JDBCType.TIMESTAMP),
                Map.entry(Short.class, JDBCType.SMALLINT),
                Map.entry(String.class, JDBCType.VARCHAR),
                Map.entry(Integer.class, JDBCType.INTEGER),
                Map.entry(Double.class, JDBCType.DOUBLE),
                Map.entry(LocalDate.class, JDBCType.DATE),
                Map.entry(Byte.class, JDBCType.TINYINT),
                Map.entry(Boolean.class, JDBCType.BIT),
                Map.entry(BigDecimal.class, JDBCType.NUMERIC)
        );
    }

    public String getJDBCType(Class<?> javaType) {
        validateSupportedType(javaType);
        return types.get(javaType).getName();
    }

    public void validateSupportedType(Class<?> objectType) {
        if (!types.containsKey(objectType)) {
            throw new IllegalArgumentException("Given type not supported");
        }
    }
}
