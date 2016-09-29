package edu.kulikov.email2telegram.spring;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 25.09.2016
 */
@Component
public class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {

    }
}
