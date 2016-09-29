package edu.kulikov.email2telegram.email.connection.impl;


import edu.kulikov.email2telegram.domain.entity.Provider;
import edu.kulikov.email2telegram.email.connection.api.ProviderChecker;
import edu.kulikov.email2telegram.email.util.Holder;
import org.springframework.stereotype.Component;

import java.net.Socket;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
@Component
public class SocketProviderChecker implements ProviderChecker {
    @Override
    public boolean isProviderAvailable(Provider provider, Holder<Exception> exception) {
        Socket socket = null;
        try {
            socket = new Socket(provider.getHost(), provider.getPort());
            return true;
        } catch (Exception ex) {
            exception.setObject(ex);
            return false;
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception ignored) {
            }
        }

    }
}
