package com.github.object.persistence.common.utils;

public final class StringUtils {

    private StringUtils() {}

    public static boolean isBlank(String string) {
        return string == null || string.isBlank();
    }

    public static String separateWithSpace(String string1, String string2) {
        return (string1 + " " + string2).intern();
    }
}
