package com.github.object.persistence.common.utils;

import java.util.*;

public class CollectionUtils {
    public static void putIfAbsent(String key, String value, Map<String, Deque<String>> result) {
        Deque<String> list = result.get(key);
        if (list == null) {
            result.put(key, new LinkedList<>(List.of(value)));
        } else {
            list.add(value);
        }
    }
}
