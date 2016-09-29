package edu.kulikov.email2telegram.bus;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 10.09.2016
 */
@Configuration
public class BusConfig {
    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }
}
