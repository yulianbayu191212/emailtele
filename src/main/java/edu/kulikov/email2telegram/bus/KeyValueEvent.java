package edu.kulikov.email2telegram.bus;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 10.09.2016
 */
public interface KeyValueEvent<K,V> extends Event {
    K getKey();
    V getValue();
}
