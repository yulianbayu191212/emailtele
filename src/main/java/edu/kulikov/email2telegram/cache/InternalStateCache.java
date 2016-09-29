package edu.kulikov.email2telegram.cache;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 10.09.2016
 */
@Target({ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("token_cache")
public @interface InternalStateCache {
}
