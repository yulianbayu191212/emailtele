package edu.kulikov.email2telegram.email.util;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
public class Holder<T> {
    private T object;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public boolean hasValue() {
        return object != null;
    }
}
