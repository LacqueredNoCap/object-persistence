package com.github.object.persistence.common.utils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final  class CollectionUtils {

    private CollectionUtils() {}

    public static <K, V> void put(K key, V value, Map<K, Deque<V>> result) {
        Deque<V> list = result.get(key);
        if (list == null) {
            result.put(key, new LinkedList<>(List.of(value)));
        } else {
            list.add(value);
        }
    }
}
