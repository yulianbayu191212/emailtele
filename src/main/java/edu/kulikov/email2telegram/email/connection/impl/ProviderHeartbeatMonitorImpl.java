package edu.kulikov.email2telegram.email.connection.impl;

import edu.kulikov.email2telegram.domain.entity.Provider;
import edu.kulikov.email2telegram.domain.service.ProviderService;
import edu.kulikov.email2telegram.email.connection.api.ProviderChecker;
import edu.kulikov.email2telegram.email.connection.api.ProviderHeartbeatListener;
import edu.kulikov.email2telegram.email.connection.api.ProviderHeartbeatMonitor;
import edu.kulikov.email2telegram.email.util.Holder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
@Component
@Slf4j
public class ProviderHeartbeatMonitorImpl implements ProviderHeartbeatMonitor {
    private ProviderChecker providerChecker;
    private ProviderService providerService;
    private List<ProviderHeartbeatListener> listeners = new ArrayList<>();
    private boolean started;
    private ScheduledExecutorService scheduledExecutorService;


    @Override
    public void startMonitor(long checkRate, TimeUnit timeUnit) {
        if (started) return;

        List<Provider> providers = providerService.findAll();

        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(
                () -> {
                    for (Provider provider : providers) {
                        Holder<Exception> exceptionHolder = new Holder<>();
                        if (!providerChecker.isProviderAvailable(
                                provider, exceptionHolder)) {
                            log.warn("Provider={} isn't available", provider.getType(), exceptionHolder.getObject());
                            listeners.forEach(l -> l.onHeartbeatFail(provider,
                                    exceptionHolder.getObject()));
                        } else {
                            log.trace("Provider={} is available", provider.getType());
                            listeners.forEach(l -> l.onHeartbeatOk(provider));
                        }
                    }
                },
                0, checkRate, timeUnit
        );
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void stopMonitor() {
        if (!started) return;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            started = false;
        }

    }

    @Override
    public void addListener(ProviderHeartbeatListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ProviderHeartbeatListener listener) {
        listeners.remove(listener);
    }


    @Autowired
    public void setProviderChecker(ProviderChecker providerChecker) {
        this.providerChecker = providerChecker;
    }

    @Autowired
    public void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }
}
