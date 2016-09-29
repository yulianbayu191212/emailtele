package edu.kulikov.email2telegram.bot.state.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 10.09.2016
 */
@Configuration
public class RouterConfig {
    @Bean
    @Scope(value = "prototype")
    public Router router() {
        return new RouterImpl();
        //TODO:register global route
    }
}
