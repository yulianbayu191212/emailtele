package edu.kulikov.email2telegram.email.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

import static org.apache.commons.lang3.tuple.Pair.of;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 20.09.2016
 */
public class TableUtil {
    public static <K, V> Pair<K, V> first(Map<K, V> map) {
        return map.entrySet().stream().
                findFirst().map(entry ->
                of(entry.getKey(), entry.getValue())).
                orElseGet(() -> null);
    }
}
