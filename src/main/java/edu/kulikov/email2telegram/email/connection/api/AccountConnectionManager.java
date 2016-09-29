package edu.kulikov.email2telegram.email.connection.api;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 17.09.2016
 */
public interface AccountConnectionManager {
    MailboxConnection registerAccount(Long accountId);
    void unregisterAccount(Long accountId);
}
