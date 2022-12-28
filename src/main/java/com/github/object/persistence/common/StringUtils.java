package com.github.object.persistence.common;

public class StringUtils {
    public static boolean isBlank(String string) {
        return string == null || string.isBlank() || string.isEmpty() || string.strip().isEmpty();
    }
}
