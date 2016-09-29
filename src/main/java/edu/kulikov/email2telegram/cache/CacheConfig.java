package edu.kulikov.email2telegram.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 10.09.2016
 */
@Configuration
public class CacheConfig {
    @Bean
    @InternalStateCache
    public Cache<String, Object> tokenCache() {
        return CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
    }
}
