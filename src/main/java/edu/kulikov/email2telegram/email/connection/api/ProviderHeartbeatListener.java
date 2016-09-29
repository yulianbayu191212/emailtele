package edu.kulikov.email2telegram.email.connection.api;

import edu.kulikov.email2telegram.domain.entity.Provider;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
public interface ProviderHeartbeatListener {
    void onHeartbeatFail(Provider provider, Exception e);
    default void onHeartbeatOk(Provider provider) {}
}
