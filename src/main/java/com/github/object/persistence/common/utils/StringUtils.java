package com.github.object.persistence.common.utils;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean isBlank(String string) {
        return string == null || string.isEmpty() || string.isBlank();
    }

    public static String separateWithSpace(String string1, String string2) {
        return String.join(" ", string1, string2);
    }
}
