package edu.kulikov.email2telegram.email.connection.api;

import edu.kulikov.email2telegram.domain.entity.Provider;
import edu.kulikov.email2telegram.email.util.Holder;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
public interface ProviderChecker {
    boolean isProviderAvailable(Provider provider, Holder<Exception> exception);
}
