package edu.kulikov.email2telegram.email.connection.api;

import java.util.concurrent.TimeUnit;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
public interface ProviderHeartbeatMonitor {
    void startMonitor(long checkRate, TimeUnit timeUnit);

    void stopMonitor();

    boolean isStarted();

    void addListener(ProviderHeartbeatListener listener);

    void removeListener(ProviderHeartbeatListener listener);
}
